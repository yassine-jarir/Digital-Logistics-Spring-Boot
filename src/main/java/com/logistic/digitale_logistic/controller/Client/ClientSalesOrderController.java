package com.logistic.digitale_logistic.controller.Client;
import com.logistic.digitale_logistic.dto.SalesOrderDTO;
import com.logistic.digitale_logistic.dto.SalesOrderWithReservationDTO;
import com.logistic.digitale_logistic.service.client.SalesOrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/client/sales-orders")
@RequiredArgsConstructor
@Tag(name = "Client - Sales Orders", description = "Client endpoints for creating and managing their sales orders")
@SecurityRequirement(name = "Bearer Authentication")
public class ClientSalesOrderController {

    private final SalesOrderService salesOrderService;

    /**
     * Create a new sales order with automatic reservation
     *
     * @param dto the sales order details
     * @return the created sales order with reservation result
     */
    @Operation(
            summary = "Create sales order",
            description = "Create a new sales order with automatic inventory reservation. The system will reserve available stock and create backorders for unavailable quantities"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Sales order created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid sales order data"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden - Client role required")
    })
    @PostMapping
    @PreAuthorize("hasRole('CLIENT')")
    @ResponseStatus(HttpStatus.CREATED)
    public SalesOrderWithReservationDTO createSalesOrder(@Valid @RequestBody SalesOrderDTO dto) {
        return salesOrderService.createSalesOrder(dto);
    }

    /**
     * Get all sales orders for the authenticated client
     *
     * @param authentication the authenticated user
     * @return list of sales orders
     */
    @Operation(
            summary = "Get my sales orders",
            description = "Retrieve all sales orders belonging to the authenticated client"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved sales orders"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden - Client role required")
    })
    @GetMapping
    @PreAuthorize("hasRole('CLIENT')")
    @ResponseStatus(HttpStatus.OK)
    public List<SalesOrderDTO> getMyOrders(Authentication authentication) {
        return salesOrderService.getMyOrders();
    }

}
