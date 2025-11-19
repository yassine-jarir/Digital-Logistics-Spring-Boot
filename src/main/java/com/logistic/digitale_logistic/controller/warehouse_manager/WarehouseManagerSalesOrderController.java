package com.logistic.digitale_logistic.controller.warehouse_manager;

import com.logistic.digitale_logistic.dto.SalesOrderDTO;
import com.logistic.digitale_logistic.service.client.SalesOrderService;
import io.swagger.v3.oas.annotations.Operation;
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
@RequestMapping("/api/warehouse-manager/sales-orders")
@RequiredArgsConstructor
@Tag(name = "Warehouse Manager - Sales Orders", description = "Warehouse Manager endpoints for viewing and managing sales orders")
@SecurityRequirement(name = "Bearer Authentication")
public class WarehouseManagerSalesOrderController {

    private final SalesOrderService salesOrderService;

    /**
     * Get all sales orders (warehouse manager can view all orders)
     *
     * @return list of all sales orders
     */
    @Operation(
            summary = "Get all sales orders",
            description = "Retrieve all sales orders for fulfillment purposes (Warehouse Manager only)"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved all sales orders"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden - Warehouse Manager role required")
    })
    @GetMapping
    @PreAuthorize("hasRole('WAREHOUSE_MANAGER')")
    @ResponseStatus(HttpStatus.OK)
    public List<SalesOrderDTO> getAllSalesOrders() {
        return salesOrderService.getAllSalesOrders();
    }


}
