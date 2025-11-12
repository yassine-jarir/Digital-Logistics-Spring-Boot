package com.logistic.digitale_logistic.service.client;

import com.logistic.digitale_logistic.dto.ShipmentDTO;
import com.logistic.digitale_logistic.entity.*;
import com.logistic.digitale_logistic.enums.MovementType;
import com.logistic.digitale_logistic.mapper.ShipmentMapper;
import com.logistic.digitale_logistic.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ShipmentService {

    private final ShipmentRepository shipmentRepository;
    private final SalesOrderRepository salesOrderRepository;
    private final InventoryRepository inventoryRepository;
    private final InventoryMovementRepository inventoryMovementRepository;
    private final ShipmentMapper shipmentMapper;

    /**
     * Create shipment for a fully or partially reserved sales order
     *
     * @param salesOrderId the sales order to ship
     * @return created shipment DTO
     */
    @Transactional
    public ShipmentDTO createShipment(Long salesOrderId) {
        log.info("Creating shipment for Sales Order ID: {}", salesOrderId);

        SalesOrder salesOrder = salesOrderRepository.findById(salesOrderId)
                .orElseThrow(() -> new IllegalArgumentException("Sales order not found with ID: " + salesOrderId));

        // Validate order status - only RESERVED orders can be shipped
        // Matches DB constraint: CREATED, RESERVED, SHIPPED, DELIVERED, CANCELLED
        if (!"RESERVED".equals(salesOrder.getStatus())) {
            throw new IllegalStateException(
                    "Cannot create shipment. Order must be RESERVED. Current status: "
                    + salesOrder.getStatus());
        }

        // Create shipment
        Shipment shipment = new Shipment();
        shipment.setShipmentNumber(generateShipmentNumber());
        shipment.setSalesOrder(salesOrder);
        shipment.setStatus("PLANNED");
        shipment.setPlannedShipDate(salesOrder.getPlannedShipDate() != null ?
                salesOrder.getPlannedShipDate() : LocalDate.now().plusDays(1));
        shipment.setCreatedAt(LocalDateTime.now());

        // Create shipment lines for reserved quantities
        List<ShipmentLine> shipmentLines = new ArrayList<>();
        for (SoLine soLine : salesOrder.getLines()) {
            if (soLine.getReservedQuantity() > 0) {
                if (soLine.getProduct() == null) {
                    throw new IllegalStateException("SoLine product is null for SoLine id: " + soLine.getId());
                }
                ShipmentLine shipmentLine = new ShipmentLine();
                shipmentLine.setShipment(shipment);
                // set the sales order line reference to satisfy NOT NULL constraint
                shipmentLine.setSalesOrderLine(soLine);
                shipmentLine.setProduct(soLine.getProduct());
                shipmentLine.setQuantity(soLine.getReservedQuantity());
                // Set quantityShipped to match quantity to satisfy DB check constraint
                // (DB likely requires quantity_shipped = quantity or quantity_shipped <= quantity and > 0)
                shipmentLine.setQuantityShipped(soLine.getReservedQuantity());
                shipmentLines.add(shipmentLine);
            }
        }

        shipment.setLines(shipmentLines);

        Shipment savedShipment = shipmentRepository.save(shipment);

        log.info("Shipment created: {}", savedShipment.getShipmentNumber());

        return shipmentMapper.toDTO(savedShipment);
    }

    /**
     * Ship the shipment - update status and process inventory movements
     *
     * @param shipmentId the shipment to ship
     * @param trackingNumber tracking number for the shipment
     * @param carrier carrier name
     * @return updated shipment DTO
     */
    @Transactional
    public ShipmentDTO shipShipment(Long shipmentId, String trackingNumber, String carrier) {
        log.info("Processing shipment ID: {}", shipmentId);

        Shipment shipment = shipmentRepository.findById(shipmentId)
                .orElseThrow(() -> new IllegalArgumentException("Shipment not found with ID: " + shipmentId));

        if (!"PLANNED".equals(shipment.getStatus())) {
            throw new IllegalStateException("Can only ship PLANNED shipments. Current status: " + shipment.getStatus());
        }

        // Process inventory movements (OUTBOUND)
        for (ShipmentLine line : shipment.getLines()) {
            processOutboundMovement(line, shipment);
        }

        // Update shipment
        shipment.setStatus("SHIPPED");
        shipment.setActualShipDate(LocalDate.now());
        shipment.setTrackingNumber(trackingNumber);
        shipment.setCarrier(carrier);
        Shipment savedShipment = shipmentRepository.save(shipment);

        // Update sales order status
        SalesOrder salesOrder = shipment.getSalesOrder();
        salesOrder.setStatus("SHIPPED");
        salesOrder.setUpdatedAt(LocalDateTime.now());
        salesOrderRepository.save(salesOrder);

        log.info("Shipment {} dispatched successfully", shipment.getShipmentNumber());

        return shipmentMapper.toDTO(savedShipment);
    }

    /**
     * Process OUTBOUND inventory movement for shipment line
     */
    private void processOutboundMovement(ShipmentLine line, Shipment shipment) {
        Product product = line.getProduct();
        Warehouse warehouse = shipment.getSalesOrder().getWarehouse();
        int quantity = line.getQuantity();

        // Get inventory
        Inventory inventory = inventoryRepository.findByProduct_IdAndWarehouse_Id(
                        product.getId(), warehouse.getId())
                .orElseThrow(() -> new IllegalStateException(
                        "Inventory not found for product " + product.getSku() + " in warehouse"));


        // Update inventory: decrease qtyOnHand and qtyReserved
        inventory.setQtyOnHand(inventory.getQtyOnHand() - quantity);
        inventory.setQtyReserved(inventory.getQtyReserved() - quantity);
        inventory.setUpdatedAt(LocalDateTime.now());
        inventoryRepository.save(inventory);

        // Create OUTBOUND movement
        InventoryMovement movement = new InventoryMovement();
        movement.setProduct(product);
        movement.setWarehouse(warehouse);
        movement.setMovementType(MovementType.OUTBOUND);
        movement.setQuantity(quantity);
        movement.setSalesOrder(shipment.getSalesOrder());
        movement.setReferenceDoc(shipment.getShipmentNumber());
        movement.setNotes("Shipped via " + (shipment.getCarrier() != null ? shipment.getCarrier() : "N/A")
                + " - Tracking: " + (shipment.getTrackingNumber() != null ? shipment.getTrackingNumber() : "N/A"));
        movement.setOccurredAt(LocalDateTime.now());
        inventoryMovementRepository.save(movement);

        // record how many units were shipped on the shipment line
        line.setQuantityShipped(quantity);

        log.debug("OUTBOUND movement recorded: Product={}, Qty={}, OnHand={}, Reserved={}",
                product.getSku(), quantity, inventory.getQtyOnHand(), inventory.getQtyReserved());
    }

    /**
     * Mark shipment as delivered
     */
    @Transactional
    public ShipmentDTO markAsDelivered(Long shipmentId) {
        log.info("Marking shipment ID: {} as delivered", shipmentId);

        Shipment shipment = shipmentRepository.findById(shipmentId)
                .orElseThrow(() -> new IllegalArgumentException("Shipment not found with ID: " + shipmentId));

        if (!"SHIPPED".equals(shipment.getStatus())) {
            throw new IllegalStateException("Can only deliver SHIPPED shipments. Current status: " + shipment.getStatus());
        }

        shipment.setStatus("DELIVERED");
        Shipment savedShipment = shipmentRepository.save(shipment);

        // Update sales order status
        SalesOrder salesOrder = shipment.getSalesOrder();

        // Check if all shipments are delivered
        boolean allDelivered = salesOrder.getShipments().stream()
                .allMatch(s -> "DELIVERED".equals(s.getStatus()));

        if (allDelivered) {
            salesOrder.setStatus("DELIVERED");
            salesOrder.setUpdatedAt(LocalDateTime.now());
            salesOrderRepository.save(salesOrder);
        }

        log.info("Shipment {} marked as delivered", shipment.getShipmentNumber());

        return shipmentMapper.toDTO(savedShipment);
    }



    private String generateShipmentNumber() {
        return "SHIP-" + System.currentTimeMillis();
    }
}
