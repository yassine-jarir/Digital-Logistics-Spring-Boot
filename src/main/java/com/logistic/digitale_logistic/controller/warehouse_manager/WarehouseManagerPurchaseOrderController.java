package com.logistic.digitale_logistic.controller.warehouse_manager;
import com.logistic.digitale_logistic.dto.PurchaseOrderDTO;
import com.logistic.digitale_logistic.dto.ReceiveLineDTO;
import com.logistic.digitale_logistic.service.warehouse_manager.PurchaseOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;
@RestController
@RequestMapping("/api/warehouse-manager/purchase-orders")
@RequiredArgsConstructor
public class WarehouseManagerPurchaseOrderController {
    private final PurchaseOrderService purchaseOrderService;
    // Create a new purchase order (DRAFT)

    @PostMapping
    @PreAuthorize("hasAnyRole('WAREHOUSE_MANAGER', 'ADMIN')")
    @ResponseStatus(HttpStatus.CREATED)
    public PurchaseOrderDTO createPurchaseOrder(@RequestBody PurchaseOrderDTO dto) {
        return purchaseOrderService.createPurchaseOrder(dto);
    }
    // Receive a purchase order (APPROVED → RECEIVED)
    @PostMapping("/{id}/receive")
    @PreAuthorize("hasAnyRole('WAREHOUSE_MANAGER', 'ADMIN')")
    public PurchaseOrderDTO receivePurchaseOrder(
            @PathVariable Long id,
            @RequestBody List<ReceiveLineDTO> receivedLines) {
        return purchaseOrderService.receivePurchaseOrder(id, receivedLines);
    }
    // Get all purchase orders for a specific warehouse
    @GetMapping("/warehouse/{warehouseId}")
    @PreAuthorize("hasAnyRole('WAREHOUSE_MANAGER', 'ADMIN')")
    public List<PurchaseOrderDTO> getPurchaseOrdersByWarehouse(@PathVariable Long warehouseId) {
        return purchaseOrderService.getPurchaseOrdersByWarehouse(warehouseId);
    }
    // Get purchase order by ID
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('WAREHOUSE_MANAGER', 'ADMIN')")
    public PurchaseOrderDTO getPurchaseOrderById(@PathVariable Long id) {
        return purchaseOrderService.getPurchaseOrderById(id);
    }
    // Cancel a purchase order (warehouse manager can cancel their own orders)
    @PatchMapping("/{id}/cancel")
    @PreAuthorize("hasAnyRole('WAREHOUSE_MANAGER', 'ADMIN')")
    public PurchaseOrderDTO cancelPurchaseOrder(@PathVariable Long id) {
        return purchaseOrderService.cancelPurchaseOrder(id);
    }
}
