package com.logistic.digitale_logistic.controller.Admin;

import com.logistic.digitale_logistic.dto.PurchaseOrderDTO;
import com.logistic.digitale_logistic.service.warehouse_manager.PurchaseOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/purchase-orders")
@RequiredArgsConstructor
public class AdminPurchaseOrderController {

    private final PurchaseOrderService purchaseOrderService;

    // Get all purchase orders
    @GetMapping
    public List<PurchaseOrderDTO> getAllPurchaseOrders() {
        return purchaseOrderService.getAllPurchaseOrders();
    }

    // Get purchase orders by status
    @GetMapping("/status/{status}")
    public List<PurchaseOrderDTO> getPurchaseOrdersByStatus(@PathVariable String status) {
        return purchaseOrderService.getPurchaseOrdersByStatus(status);
    }

    // Get purchase order by ID
    @GetMapping("/{id}")
    public PurchaseOrderDTO getPurchaseOrderById(@PathVariable Long id) {
        return purchaseOrderService.getPurchaseOrderById(id);
    }

    // Approve a purchase order (DRAFT → APPROVED)
    @PatchMapping("/{id}/approve")
    public PurchaseOrderDTO approvePurchaseOrder(@PathVariable Long id) {
        return purchaseOrderService.approvePurchaseOrder(id);
    }

    // Cancel a purchase order
    @PatchMapping("/{id}/cancel")
    public PurchaseOrderDTO cancelPurchaseOrder(@PathVariable Long id) {
        return purchaseOrderService.cancelPurchaseOrder(id);
    }
}
