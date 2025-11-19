package com.logistic.digitale_logistic.controller.warehouse_manager;
import com.logistic.digitale_logistic.dto.PurchaseOrderDTO;
import com.logistic.digitale_logistic.service.warehouse_manager.PurchaseOrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;
@RestController
@RequestMapping("/api/warehouse-manager/purchase-orders")
@RequiredArgsConstructor
@Tag(name = "Warehouse Manager - Purchase Orders", description = "Warehouse Manager endpoints for creating and receiving purchase orders")
@SecurityRequirement(name = "Bearer Authentication")
public class WarehouseManagerPurchaseOrderController {
    private final PurchaseOrderService purchaseOrderService;
    // Create a new purchase order (DRAFT)
    @Operation(
            summary = "Create purchase order",
            description = "Create a new purchase order in DRAFT status. Must be approved by admin before receiving"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Purchase order created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid purchase order data"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden - Warehouse Manager or Admin role required")
    })
    @PostMapping
    @PreAuthorize("hasAnyRole('WAREHOUSE_MANAGER', 'ADMIN')")
    @ResponseStatus(HttpStatus.CREATED)
    public PurchaseOrderDTO createPurchaseOrder(@RequestBody PurchaseOrderDTO dto) {
        return purchaseOrderService.createPurchaseOrder(dto);
    }
    // Receive a purchase order - automatically receives ALL lines with full quantities
    // Just pass the PO ID - no request body needed!
    @Operation(
            summary = "Receive entire purchase order",
            description = "Receive all items in a purchase order. Automatically receives all lines with full quantities and updates inventory"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Purchase order received successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid purchase order status or not approved"),
            @ApiResponse(responseCode = "404", description = "Purchase order not found"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden - Warehouse Manager or Admin role required")
    })
    @PostMapping("/{id}/receive")
    @PreAuthorize("hasAnyRole('WAREHOUSE_MANAGER', 'ADMIN')")
    public PurchaseOrderDTO receivePurchaseOrder(
            @Parameter(description = "Purchase Order ID", required = true, example = "1")
            @PathVariable Long id) {
        return purchaseOrderService.receiveEntirePurchaseOrder(id);
    }

    // Get purchase order by ID
    @Operation(
            summary = "Get purchase order by ID",
            description = "Retrieve detailed information about a specific purchase order"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Purchase order found"),
            @ApiResponse(responseCode = "404", description = "Purchase order not found"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden - Warehouse Manager or Admin role required")
    })
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('WAREHOUSE_MANAGER', 'ADMIN')")
    public PurchaseOrderDTO getPurchaseOrderById(
            @Parameter(description = "Purchase Order ID", required = true, example = "1")
            @PathVariable Long id) {
        return purchaseOrderService.getPurchaseOrderById(id);
    }

}
