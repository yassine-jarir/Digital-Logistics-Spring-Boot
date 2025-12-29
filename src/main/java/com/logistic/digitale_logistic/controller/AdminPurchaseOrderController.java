package com.logistic.digitale_logistic.controller;

import com.logistic.digitale_logistic.dto.PurchaseOrderDTO;
import com.logistic.digitale_logistic.enums.PurchaseOrderStatus;
import com.logistic.digitale_logistic.service.warehouse_manager.PurchaseOrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/purchase-orders")
@RequiredArgsConstructor
@Tag(name = "Admin - Purchase Orders", description = "Admin endpoints for managing purchase orders")
@SecurityRequirement(name = "Bearer Authentication")
public class AdminPurchaseOrderController {

    private final PurchaseOrderService purchaseOrderService;
Boolean a = false;
    /**
     * Get all purchase orders (with optional status filter)
     *
     * @param status the status to filter by (optional)
     * @return the list of purchase orders
     */
    @Operation(
            summary = "Get all purchase orders",
            description = "Retrieve all purchase orders with optional status filter. Available statuses: DRAFT, APPROVED, RECEIVED, CANCELLED"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved purchase orders"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid or missing JWT token"),
            @ApiResponse(responseCode = "403", description = "Forbidden - Admin role required")
    })
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.OK)
    public List<PurchaseOrderDTO> getAllPurchaseOrders(
            @Parameter(description = "Filter by purchase order status (optional)", example = "APPROVED")
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
    @Operation(
            summary = "Get purchase order by ID",
            description = "Retrieve detailed information about a specific purchase order"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Purchase order found"),
            @ApiResponse(responseCode = "404", description = "Purchase order not found"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden - Admin role required")
    })
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.OK)
    public PurchaseOrderDTO getPurchaseOrderById(
            @Parameter(description = "Purchase order ID", required = true, example = "1")
            @PathVariable Long id) {
        return purchaseOrderService.getPurchaseOrderById(id);
    }

    /**
     * Approve a purchase order (DRAFT → APPROVED)
     *
     * @param id the ID of the purchase order
     * @return the approved purchase order
     */
    @Operation(
            summary = "Approve purchase order",
            description = "Approve a purchase order, changing status from DRAFT to APPROVED"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Purchase order approved successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid status transition"),
            @ApiResponse(responseCode = "404", description = "Purchase order not found"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden - Admin role required")
    })
    @PatchMapping("/{id}/approve")
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.OK)
    public PurchaseOrderDTO approvePurchaseOrder(
            @Parameter(description = "Purchase order ID to approve", required = true, example = "1")
            @PathVariable Long id) {
        return purchaseOrderService.approvePurchaseOrder(id);
    }


}
