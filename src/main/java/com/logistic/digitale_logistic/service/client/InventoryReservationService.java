package com.logistic.digitale_logistic.service.client;

import com.logistic.digitale_logistic.dto.BackorderDTO;
import com.logistic.digitale_logistic.dto.ReservationResultDTO;
import com.logistic.digitale_logistic.entity.*;
import com.logistic.digitale_logistic.enums.BackorderStatus;
import com.logistic.digitale_logistic.enums.MovementType;
import com.logistic.digitale_logistic.enums.PurchaseOrderStatus;
import com.logistic.digitale_logistic.mapper.BackorderMapper;
import com.logistic.digitale_logistic.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class InventoryReservationService {

    private final InventoryRepository inventoryRepository;
    private final InventoryMovementRepository inventoryMovementRepository;
    private final BackorderRepository backorderRepository;
    private final SalesOrderRepository salesOrderRepository;
    private final SoLineRepository soLineRepository;
    private final PurchaseOrderRepository purchaseOrderRepository;
    private final SupplierRepository supplierRepository;
    private final BackorderMapper backorderMapper;

    /**
     * Main entry point for automatic reservation and backorder creation
     *
     * @param salesOrderId the sales order to process
     * @return reservation result with backorder information
     */
    @Transactional
    public ReservationResultDTO processOrderReservation(Long salesOrderId) {
        log.info("Starting reservation process for Sales Order ID: {}", salesOrderId);

        SalesOrder salesOrder = salesOrderRepository.findById(salesOrderId)
                .orElseThrow(() -> new IllegalArgumentException("Sales order not found with ID: " + salesOrderId));

        if (!"CREATED".equals(salesOrder.getStatus())) {
            throw new IllegalStateException("Sales order must be in CREATED status. Current status: " + salesOrder.getStatus());
        }

        List<BackorderDTO> backorders = new ArrayList<>();
        boolean hasBackorders = false;
        boolean fullyReserved = true;

        // Process each line
        for (SoLine soLine : salesOrder.getLines()) {
            ReservationLineResult lineResult = processLineReservation(soLine, salesOrder);

            if (lineResult.hasBackorder()) {
                hasBackorders = true;
                fullyReserved = false;
                backorders.addAll(lineResult.getBackorders());
            }

            if (lineResult.getReservedQuantity() < soLine.getOrderedQuantity()) {
                fullyReserved = false;
            }
        }

        // Update sales order status
        String newStatus = determineOrderStatus(salesOrder);
        salesOrder.setStatus(newStatus);
        salesOrder.setUpdatedAt(LocalDateTime.now());
        salesOrderRepository.save(salesOrder);

        log.info("Reservation completed for SO-{}: Status={}, FullyReserved={}, HasBackorders={}",
                salesOrder.getOrderNumber(), newStatus, fullyReserved, hasBackorders);

        return ReservationResultDTO.builder()
                .salesOrderId(salesOrderId)
                .salesOrderNumber(salesOrder.getOrderNumber())
                .status(newStatus)
                .fullyReserved(fullyReserved)
                .hasBackorders(hasBackorders)
                .backorders(backorders)
                .message(buildResultMessage(fullyReserved, hasBackorders, backorders.size()))
                .build();
    }

    /**
     * Process reservation for a single SO line
     */
    private ReservationLineResult processLineReservation(SoLine soLine, SalesOrder salesOrder) {
        Product product = soLine.getProduct();
        Warehouse warehouse = salesOrder.getWarehouse();
        int requestedQuantity = soLine.getOrderedQuantity();

        log.debug("Processing line - Product: {}, Requested: {}", product.getSku(), requestedQuantity);

        // Get current inventory
        Optional<Inventory> inventoryOpt = inventoryRepository.findByProduct_IdAndWarehouse_Id(
                product.getId(), warehouse.getId());

        int reservedQty = 0;
        List<BackorderDTO> backorders = new ArrayList<>();

        if (inventoryOpt.isPresent()) {
            Inventory inventory = inventoryOpt.get();
            int available = inventory.getQtyAvailable();

            log.debug("Available stock: {}", available);

            if (available >= requestedQuantity) {
                // Full reservation
                reservedQty = requestedQuantity;
                reserveInventory(inventory, reservedQty, salesOrder, product);
                log.info("Fully reserved {} units of {}", reservedQty, product.getSku());
            } else if (available > 0) {
                // Partial reservation
                reservedQty = available;
                reserveInventory(inventory, reservedQty, salesOrder, product);

                int backorderQty = requestedQuantity - reservedQty;
                Backorder backorder = createBackorder(soLine, product, warehouse, backorderQty);
                backorders.add(convertToDTO(backorder));

                log.info("Partially reserved {} units, backordered {} units of {}",
                        reservedQty, backorderQty, product.getSku());
            } else {
                // No stock available - full backorder
                Backorder backorder = createBackorder(soLine, product, warehouse, requestedQuantity);
                backorders.add(convertToDTO(backorder));

                // Trigger automatic PO
                triggerAutomaticPurchaseOrder(backorder, product, warehouse, requestedQuantity);

                log.info("No stock available, created full backorder for {} units of {}",
                        requestedQuantity, product.getSku());
            }
        } else {
            // No inventory record - create backorder and trigger PO
            Backorder backorder = createBackorder(soLine, product, warehouse, requestedQuantity);
            backorders.add(convertToDTO(backorder));

            triggerAutomaticPurchaseOrder(backorder, product, warehouse, requestedQuantity);

            log.info("No inventory record found, created backorder and triggered PO for {} units of {}",
                    requestedQuantity, product.getSku());
        }

        // Update SO line reserved quantity
        soLine.setReservedQuantity(reservedQty);
        soLineRepository.save(soLine);

        return new ReservationLineResult(reservedQty, backorders);
    }

    /**
     * Reserve inventory and create movement record
     */
    private void reserveInventory(Inventory inventory, int quantity, SalesOrder salesOrder, Product product) {
        // Update inventory - increase reserved, which automatically decreases available
        inventory.setQtyReserved(inventory.getQtyReserved() + quantity);
        inventory.setUpdatedAt(LocalDateTime.now());
        inventoryRepository.save(inventory);

        // Note: We don't create a RESERVED movement here because the database
        // only supports INBOUND, OUTBOUND, and ADJUSTMENT movement types.
        // The reservation is tracked through inventory.qty_reserved field.
        // When the order ships, an OUTBOUND movement will be created.

        log.debug("Inventory reserved: Product={}, Warehouse={}, Qty={}, NewReserved={}",
                product.getSku(), inventory.getWarehouse().getName(), quantity, inventory.getQtyReserved());
    }

    /**
     * Create a backorder record
     */
    private Backorder createBackorder(SoLine soLine, Product product, Warehouse warehouse, int quantity) {
        Backorder backorder = new Backorder();
        backorder.setSoLine(soLine);
        backorder.setProduct(product);
        backorder.setWarehouse(warehouse);
        backorder.setQuantityBackordered(quantity);
        backorder.setQuantityFulfilled(0);
        backorder.setStatus(BackorderStatus.PENDING);
        backorder.setNotes("Auto-created during order reservation");
        backorder.setCreatedAt(LocalDateTime.now());

        return backorderRepository.save(backorder);
    }

    /**
     * Convert Backorder entity to DTO
     */
    private BackorderDTO convertToDTO(Backorder backorder) {
        return BackorderDTO.builder()
                .id(backorder.getId())
                .soLineId(backorder.getSoLine().getId())
                .productId(backorder.getProduct().getId())
                .productName(backorder.getProduct().getName())
                .productSku(backorder.getProduct().getSku())
                .warehouseId(backorder.getWarehouse().getId())
                .warehouseName(backorder.getWarehouse().getName())
                .quantityBackordered(backorder.getQuantityBackordered())
                .quantityFulfilled(backorder.getQuantityFulfilled())
                .status(backorder.getStatus())
                .createdAt(backorder.getCreatedAt())
                .fulfilledAt(backorder.getFulfilledAt())
                .triggeredPurchaseOrderId(backorder.getTriggeredPurchaseOrder() != null ?
                        backorder.getTriggeredPurchaseOrder().getId() : null)
                .notes(backorder.getNotes())
                .build();
    }

    /**
     * Trigger automatic purchase order to supplier when no stock is available
     */
    private void triggerAutomaticPurchaseOrder(Backorder backorder, Product product,
                                               Warehouse warehouse, int quantity) {
        log.info("Triggering automatic PO for Product: {}, Quantity: {}", product.getSku(), quantity);

        // Get default supplier for product (simplified - you may have product-supplier mapping)
        Supplier supplier = getSupplierForProduct();

        if (supplier == null) {
            log.warn("No supplier found for product {}. Cannot create automatic PO.", product.getSku());
            backorder.setNotes(backorder.getNotes() + " | WARNING: No supplier found for automatic PO");
            backorderRepository.save(backorder);
            return;
        }

        // Create Purchase Order
        PurchaseOrder po = new PurchaseOrder();
        po.setPoNumber(generatePONumber());
        po.setSupplier(supplier);
        po.setWarehouse(warehouse);
        po.setStatus(PurchaseOrderStatus.DRAFT);
        po.setOrderDate(LocalDate.now());
        po.setCreatedAt(LocalDateTime.now());
        po.setUpdatedAt(LocalDateTime.now());

        // Create PO Line
        PoLine poLine = new PoLine();
        poLine.setPurchaseOrder(po);
        poLine.setProduct(product);
        poLine.setOrderedQuantity(calculatePurchaseQuantity(product, warehouse, quantity));
        poLine.setReceivedQuantity(0);
        poLine.setUnitCost(product.getCostPrice());

        po.setLines(List.of(poLine));

        PurchaseOrder savedPO = purchaseOrderRepository.save(po);

        // Link backorder to PO
        backorder.setTriggeredPurchaseOrder(savedPO);
        backorderRepository.save(backorder);

        log.info("Created automatic PO: {} for backorder", savedPO.getPoNumber());
    }

    /**
     * Get supplier for a product (simplified version)
     */
    private Supplier getSupplierForProduct() {
        // Simplified: get first active supplier
        return supplierRepository.findAll().stream()
                .filter(Supplier::getActive)
                .findFirst()
                .orElse(null);
    }

    /**
     * Calculate purchase quantity considering safety stock and pending backorders
     */
    private int calculatePurchaseQuantity(Product product, Warehouse warehouse, int backorderQty) {
        // Get total pending backorders for this product in this warehouse
        Integer totalPendingBackorders = backorderRepository.getTotalPendingBackorderQuantity(
                product.getId(), warehouse.getId());

        // Purchase enough to cover all pending backorders plus some buffer
        int safetyStock = 10; // Configurable safety stock
        return Math.max(backorderQty, totalPendingBackorders != null ? totalPendingBackorders : 0) + safetyStock;
    }

    /**
     * Determine sales order status based on line reservations
     */
    private String determineOrderStatus(SalesOrder salesOrder) {
        // Check if ANY lines have been reserved
        boolean anyReserved = salesOrder.getLines().stream()
                .anyMatch(line -> line.getReservedQuantity() > 0);

        // If any stock is reserved, mark as RESERVED (even if partial)
        // This matches your DB constraint: CREATED, RESERVED, SHIPPED, DELIVERED, CANCELLED
        if (anyReserved) {
            return "RESERVED";
        } else {
            return "CREATED";
        }
    }

    private boolean hasPartialReservations(SalesOrder salesOrder) {
        return salesOrder.getLines().stream()
                .anyMatch(line -> line.getReservedQuantity() > 0);
    }

    private String buildResultMessage(boolean fullyReserved, boolean hasBackorders, int backorderCount) {
        if (fullyReserved) {
            return "Order fully reserved and ready for shipment.";
        } else if (hasBackorders) {
            return String.format("Order partially reserved. %d backorder(s) created. " +
                    "Automatic purchase orders triggered where needed.", backorderCount);
        } else {
            return "Order reservation completed with partial availability.";
        }
    }

    private String generatePONumber() {
        return "PO-AUTO-" + System.currentTimeMillis();
    }

    /**
     * Internal class to hold line reservation results
     */
    private static class ReservationLineResult {
        private final int reservedQuantity;
        private final List<BackorderDTO> backorders;

        public ReservationLineResult(int reservedQuantity, List<BackorderDTO> backorders) {
            this.reservedQuantity = reservedQuantity;
            this.backorders = backorders;
        }

        public int getReservedQuantity() {
            return reservedQuantity;
        }

        public List<BackorderDTO> getBackorders() {
            return backorders;
        }

        public boolean hasBackorder() {
            return !backorders.isEmpty();
        }
    }
}
