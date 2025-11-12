package com.logistic.digitale_logistic.controller.Admin;

import com.logistic.digitale_logistic.dto.PurchaseOrderDTO;
import com.logistic.digitale_logistic.enums.PurchaseOrderStatus;
import com.logistic.digitale_logistic.service.warehouse_manager.PurchaseOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/purchase-orders")
@RequiredArgsConstructor
public class AdminPurchaseOrderController {

    private final PurchaseOrderService purchaseOrderService;

    /**
     * Get all purchase orders (with optional status filter)
     *
     * @param status the status to filter by (optional)
     * @return the list of purchase orders
     */
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.OK)
    public List<PurchaseOrderDTO> getAllPurchaseOrders(
            @RequestParam(required = false) String status) {
        if (status != null && !status.isEmpty()) {
            return purchaseOrderService.getPurchaseOrdersByStatus(PurchaseOrderStatus.valueOf(status));
        }
        return purchaseOrderService.getAllPurchaseOrders();
    }

    /**
     * Get purchase order by ID
     *
     * @param id the ID of the purchase order
     * @return the purchase order
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.OK)
    public PurchaseOrderDTO getPurchaseOrderById(@PathVariable Long id) {
        return purchaseOrderService.getPurchaseOrderById(id);
    }

    /**
     * Approve a purchase order (DRAFT → APPROVED)
     *
     * @param id the ID of the purchase order
     * @return the approved purchase order
     */
    @PatchMapping("/{id}/approve")
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.OK)
    public PurchaseOrderDTO approvePurchaseOrder(@PathVariable Long id) {
        return purchaseOrderService.approvePurchaseOrder(id);
    }


}
