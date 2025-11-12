package com.logistic.digitale_logistic.service.client;

import com.logistic.digitale_logistic.entity.*;
import com.logistic.digitale_logistic.enums.BackorderStatus;
import com.logistic.digitale_logistic.enums.MovementType;
import com.logistic.digitale_logistic.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class BackorderFulfillmentService {

    private final BackorderRepository backorderRepository;
    private final InventoryRepository inventoryRepository;
    private final InventoryMovementRepository inventoryMovementRepository;
    private final SoLineRepository soLineRepository;
    private final SalesOrderRepository salesOrderRepository;

    /**
     * Automatically allocate incoming stock to pending backorders (FIFO)
     * Called when supplier delivers stock (INBOUND movement)
     *
     * @param productId the product that received stock
     * @param warehouseId the warehouse where stock arrived
     * @param receivedQuantity the quantity received
     * @param purchaseOrder the purchase order (optional)
     */
    @Transactional
    public void processPendingBackorders(Long productId, Long warehouseId,
                                        int receivedQuantity, PurchaseOrder purchaseOrder) {
        log.info("Processing pending backorders for Product ID: {}, Warehouse ID: {}, Qty: {}",
                productId, warehouseId, receivedQuantity);

        // Get pending backorders ordered by creation date (FIFO)
        List<Backorder> pendingBackorders = backorderRepository
                .findPendingBackordersByProductAndWarehouse(productId, warehouseId);

        if (pendingBackorders.isEmpty()) {
            log.info("No pending backorders found for this product/warehouse combination.");
            return;
        }

        int remainingStock = receivedQuantity;

        for (Backorder backorder : pendingBackorders) {
            if (remainingStock <= 0) {
                break;
            }

            int pendingQty = backorder.getQuantityBackordered() - backorder.getQuantityFulfilled();
            int toFulfill = Math.min(pendingQty, remainingStock);

            fulfillBackorder(backorder, toFulfill, purchaseOrder);
            remainingStock -= toFulfill;

            log.info("Fulfilled {} units for Backorder ID: {}", toFulfill, backorder.getId());
        }

        log.info("Backorder processing completed. Remaining stock: {}", remainingStock);
    }

    /**
     * Fulfill a backorder (partial or full)
     */
    private void fulfillBackorder(Backorder backorder, int quantity, PurchaseOrder purchaseOrder) {
        SoLine soLine = backorder.getSoLine();
        Product product = backorder.getProduct();
        Warehouse warehouse = backorder.getWarehouse();

        // Get or create inventory record
        Inventory inventory = inventoryRepository
                .findByProduct_IdAndWarehouse_Id(product.getId(), warehouse.getId())
                .orElseGet(() -> {
                    Inventory newInv = new Inventory();
                    newInv.setProduct(product);
                    newInv.setWarehouse(warehouse);
                    newInv.setQtyOnHand(0);
                    newInv.setQtyReserved(0);
                    newInv.setUpdatedAt(LocalDateTime.now());
                    return inventoryRepository.save(newInv);
                });

        // Reserve the stock for the backorder (increase reserved quantity)
        inventory.setQtyReserved(inventory.getQtyReserved() + quantity);
        inventory.setUpdatedAt(LocalDateTime.now());
        inventoryRepository.save(inventory);

        // Update SO line reserved quantity
        soLine.setReservedQuantity(soLine.getReservedQuantity() + quantity);
        soLineRepository.save(soLine);

        // Update backorder
        backorder.setQuantityFulfilled(backorder.getQuantityFulfilled() + quantity);

        if (backorder.getQuantityFulfilled().equals(backorder.getQuantityBackordered())) {
            backorder.setStatus(BackorderStatus.FULFILLED);
            backorder.setFulfilledAt(LocalDateTime.now());
        } else {
            backorder.setStatus(BackorderStatus.PARTIALLY_FULFILLED);
        }

        backorderRepository.save(backorder);

        // Note: We don't create a RESERVED movement here because the database
        // only supports INBOUND, OUTBOUND, and ADJUSTMENT movement types.
        // The reservation is tracked through inventory.qty_reserved field.

        // Update sales order status if needed
        updateSalesOrderStatus(soLine.getSalesOrder());

        log.debug("Backorder fulfillment recorded: BO-{}, Qty={}, Reserved={}",
                backorder.getId(), quantity, inventory.getQtyReserved());
    }

    /**
     * Update sales order status based on line reservations
     */
    private void updateSalesOrderStatus(SalesOrder salesOrder) {
        boolean anyReserved = salesOrder.getLines().stream()
                .anyMatch(line -> line.getReservedQuantity() > 0);

        String currentStatus = salesOrder.getStatus();
        String newStatus = currentStatus;

        // Only transition from CREATED to RESERVED when any stock is reserved
        // Matches DB constraint: CREATED, RESERVED, SHIPPED, DELIVERED, CANCELLED
        if (anyReserved && "CREATED".equals(currentStatus)) {
            newStatus = "RESERVED";
        }

        if (!currentStatus.equals(newStatus)) {
            salesOrder.setStatus(newStatus);
            salesOrder.setUpdatedAt(LocalDateTime.now());
            salesOrderRepository.save(salesOrder);
            log.info("Updated Sales Order {} status: {} -> {}",
                    salesOrder.getOrderNumber(), currentStatus, newStatus);
        }
    }

    /**
     * Get all pending backorders for a sales order
     */
    @Transactional(readOnly = true)
    public List<Backorder> getBackordersForSalesOrder(Long salesOrderId) {
        return backorderRepository.findBySalesOrderId(salesOrderId);
    }

 
}
