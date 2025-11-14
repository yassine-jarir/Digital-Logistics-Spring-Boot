package com.logistic.digitale_logistic.controller.Admin;

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
@RequestMapping("/api/admin/sales-orders")
@RequiredArgsConstructor
@Tag(name = "Admin - Sales Orders", description = "Admin endpoints for viewing all sales orders")
@SecurityRequirement(name = "Bearer Authentication")
public class AdminSalesOrderController {

    private final SalesOrderService salesOrderService;

    /**
     * Get all sales orders (admin can view all orders)
     *
     * @return list of all sales orders
     */
    @Operation(
            summary = "Get all sales orders",
            description = "Retrieve all sales orders from all clients (Admin only)"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved all sales orders"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid or missing JWT token"),
            @ApiResponse(responseCode = "403", description = "Forbidden - Admin role required")
    })
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.OK)
    public List<SalesOrderDTO> getAllSalesOrders() {
        return salesOrderService.getAllSalesOrders();
    }


}
