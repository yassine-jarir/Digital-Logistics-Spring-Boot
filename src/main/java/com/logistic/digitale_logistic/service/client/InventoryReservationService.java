package com.logistic.digitale_logistic.service.client;

import com.logistic.digitale_logistic.dto.BackorderDTO;
import com.logistic.digitale_logistic.dto.ReservationResultDTO;
import com.logistic.digitale_logistic.entity.*;
import com.logistic.digitale_logistic.enums.BackorderStatus;
import com.logistic.digitale_logistic.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class InventoryReservationService {

    private final InventoryRepository inventoryRepository;
    private final BackorderRepository backorderRepository;
    private final SalesOrderRepository salesOrderRepository;
    private final SoLineRepository soLineRepository;

    /**
     * Main reservation process - returns structured response for Postman
     */
    @Transactional
    public ReservationResultDTO processOrderReservation(Long salesOrderId) {
        log.info("Starting reservation process for Sales Order ID: {}", salesOrderId);

        SalesOrder salesOrder = salesOrderRepository.findById(salesOrderId)
                .orElseThrow(() -> new IllegalArgumentException("Sales order not found with ID: " + salesOrderId));

        if (!"CREATED".equals(salesOrder.getStatus())) {
            throw new IllegalStateException("Sales order must be in CREATED status. Current status: " + salesOrder.getStatus());
        }

        Warehouse selectedWarehouse = salesOrder.getWarehouse();
        List<BackorderDTO> backorders = new ArrayList<>();
        List<String> fullyReservedProducts = new ArrayList<>();
        List<String> partiallyReservedProducts = new ArrayList<>();
        List<String> noStockProducts = new ArrayList<>();

        boolean anyReserved = false;
        boolean fullyReserved = true;

        // Process each line
        for (SoLine soLine : salesOrder.getLines()) {
            Product product = soLine.getProduct();
            int requestedQty = soLine.getOrderedQuantity();

            log.debug("Processing product: {} - Requested: {}", product.getSku(), requestedQty);

            int reservedQty = 0;
            int remainingQty = requestedQty;

            // STEP 1: Check selected warehouse first
            Inventory selectedInventory = inventoryRepository.findByProduct_IdAndWarehouse_Id(
                    product.getId(), selectedWarehouse.getId()).orElse(null);

            if (selectedInventory != null && selectedInventory.getQtyAvailable() > 0) {
                int toReserve = Math.min(selectedInventory.getQtyAvailable(), remainingQty);
                reserveInventory(selectedInventory, toReserve);
                reservedQty += toReserve;
                remainingQty -= toReserve;
                log.info("Reserved {} units of {} from selected warehouse: {}",
                        toReserve, product.getSku(), selectedWarehouse.getName());
            }

            // STEP 2: Check other warehouses if needed
            if (remainingQty > 0) {
                List<Inventory> otherInventories = inventoryRepository.findAllByProductIdOrderByQtyAvailableDesc(product.getId())
                        .stream()
                        .filter(inv -> !inv.getWarehouse().getId().equals(selectedWarehouse.getId()))
                        .toList();

                for (Inventory inv : otherInventories) {
                    if (remainingQty <= 0) break;
                    int available = inv.getQtyAvailable();
                    if (available > 0) {
                        int toReserve = Math.min(available, remainingQty);
                        reserveInventory(inv, toReserve);
                        reservedQty += toReserve;
                        remainingQty -= toReserve;
                        log.info("Reserved {} units of {} from other warehouse: {}",
                                toReserve, product.getSku(), inv.getWarehouse().getName());
                    }
                }
            }

            // STEP 3: Categorize result and handle backorders
            if (reservedQty == 0) {
                // No stock available anywhere
                noStockProducts.add(product.getSku());
                fullyReserved = false;
                log.warn("No stock available for product: {}", product.getSku());

            } else if (remainingQty > 0) {
                // Partial reservation
                partiallyReservedProducts.add(product.getSku());
                anyReserved = true;
                fullyReserved = false;

                // Create backorder for remaining quantity
                Backorder backorder = createBackorder(soLine, product, selectedWarehouse, remainingQty);
                backorders.add(convertToBackorderDTO(backorder));
                log.info("Partially reserved {} units, backordered {} units for product: {}",
                        reservedQty, remainingQty, product.getSku());

            } else {
                // Full reservation
                fullyReservedProducts.add(product.getSku());
                anyReserved = true;
                log.info("Fully reserved {} units for product: {}", reservedQty, product.getSku());
            }

            // Update SO line reserved quantity
            soLine.setReservedQuantity(reservedQty);
            soLineRepository.save(soLine);
        }

        // Update sales order status
        String newStatus = anyReserved ? "RESERVED" : "CREATED";
        salesOrder.setStatus(newStatus);
        salesOrder.setUpdatedAt(LocalDateTime.now());
        salesOrderRepository.save(salesOrder);

        // Build response with clear messages
        String message = buildResponseMessage(
                fullyReservedProducts,
                partiallyReservedProducts,
                noStockProducts,
                selectedWarehouse.getName()
        );

        log.info("Reservation completed for SO-{}: Status={}, Message={}",
                salesOrder.getOrderNumber(), newStatus, message);

        return ReservationResultDTO.builder()
                .salesOrderId(salesOrderId)
                .salesOrderNumber(salesOrder.getOrderNumber())
                .status(newStatus)
                .fullyReserved(fullyReserved && anyReserved)
                .hasBackorders(!backorders.isEmpty())
                .backorders(backorders)
                .message(message)
                .build();
    }

    /**
     * Reserve inventory and update qtyReserved
     */
    private void reserveInventory(Inventory inventory, int qty) {
        inventory.setQtyReserved(inventory.getQtyReserved() + qty);
        inventory.setUpdatedAt(LocalDateTime.now());
        inventoryRepository.save(inventory);
    }

    /**
     * Create backorder record
     */
    private Backorder createBackorder(SoLine soLine, Product product, Warehouse warehouse, int qty) {
        Backorder backorder = new Backorder();
        backorder.setSoLine(soLine);
        backorder.setProduct(product);
        backorder.setWarehouse(warehouse);
        backorder.setQuantityBackordered(qty);
        backorder.setQuantityFulfilled(0);
        backorder.setStatus(BackorderStatus.PENDING);
        backorder.setNotes("Auto-created during order reservation - partial stock available");
        backorder.setCreatedAt(LocalDateTime.now());
        return backorderRepository.save(backorder);
    }

    /**
     * Convert Backorder entity to DTO
     */
    private BackorderDTO convertToBackorderDTO(Backorder backorder) {
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
     * Build clear response message for Postman based on reservation results
     */
    private String buildResponseMessage(List<String> fullyReserved,
                                       List<String> partiallyReserved,
                                       List<String> noStock,
                                       String selectedWarehouse) {

        List<String> messages = new ArrayList<>();

        // Message for fully reserved products
        if (!fullyReserved.isEmpty()) {
            messages.add(String.format("✅ Fully reserved: %s", String.join(", ", fullyReserved)));
        }

        // Message for partially reserved products (with backorders)
        if (!partiallyReserved.isEmpty()) {
            messages.add(String.format("⚠️ Partially reserved (backorders created): %s",
                    String.join(", ", partiallyReserved)));
        }

        // Message for products with no stock
        if (!noStock.isEmpty()) {
            messages.add(String.format("❌ No stock available: %s - Manager must contact supplier",
                    String.join(", ", noStock)));
        }

        // Overall summary
        if (messages.isEmpty()) {
            return "No products were processed";
        }

        String summary;
        if (noStock.isEmpty() && partiallyReserved.isEmpty()) {
            summary = "Order fully reserved and ready for shipment from warehouse: " + selectedWarehouse;
        } else if (!fullyReserved.isEmpty() && !partiallyReserved.isEmpty() && noStock.isEmpty()) {
            summary = "Order partially reserved. Some items have backorders. Check backorders list for details.";
        } else if (!noStock.isEmpty() && fullyReserved.isEmpty() && partiallyReserved.isEmpty()) {
            summary = "No stock available in any warehouse. Manager should contact supplier to arrange stock replenishment.";
        } else {
            summary = "Mixed reservation result. Some items reserved, some backordered, some unavailable.";
        }

        return summary + " | " + String.join(" | ", messages);
    }
}
