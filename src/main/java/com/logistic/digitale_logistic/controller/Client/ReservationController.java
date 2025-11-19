package com.logistic.digitale_logistic.controller.Client;

import com.logistic.digitale_logistic.dto.ReservationRequestDTO;
import com.logistic.digitale_logistic.dto.ReservationResultDTO;
import com.logistic.digitale_logistic.service.client.InventoryReservationService;
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

@RestController
@RequestMapping("/api/client/reservations")
@RequiredArgsConstructor
@Tag(name = "Client - Reservations", description = "Client endpoints for processing inventory reservations and backorders")
@SecurityRequirement(name = "Bearer Authentication")
public class ReservationController {

    private final InventoryReservationService inventoryReservationService;

    /**
     * Process automatic reservation and backorder creation for a sales order
     *
     * This endpoint:
     * - Checks available stock in the warehouse
     * - Reserves available quantities
     * - Creates backorders for unavailable quantities
     * - Triggers automatic POs when no stock is available
     * - Updates inventory and creates movement records
     *
     * @param request the reservation request containing sales order ID
     * @return reservation result with status and backorder information
     */
    @Operation(
            summary = "Process reservation",
            description = "Process inventory reservation for a sales order. Automatically reserves available stock and creates backorders for unavailable quantities. May trigger purchase orders if needed"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Reservation processed successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid sales order or reservation failed"),
            @ApiResponse(responseCode = "404", description = "Sales order not found"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden - Client role required")
    })
    @PostMapping("/process")
    @PreAuthorize("hasRole('CLIENT')")
    @ResponseStatus(HttpStatus.OK)
    public ReservationResultDTO processReservation(@RequestBody ReservationRequestDTO request) {
        return inventoryReservationService.processOrderReservation(request.getSalesOrderId());
    }

    /**
     * Process reservation by sales order ID directly
     *
     * @param salesOrderId the sales order ID to process
     * @return reservation result
     */
    @Operation(
            summary = "Process reservation by order ID",
            description = "Process inventory reservation using sales order ID directly in path"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Reservation processed successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid sales order or reservation failed"),
            @ApiResponse(responseCode = "404", description = "Sales order not found"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden - Client role required")
    })
    @PostMapping("/{salesOrderId}/process")
    @PreAuthorize("hasRole('CLIENT')")
    @ResponseStatus(HttpStatus.OK)
    public ReservationResultDTO processReservationById(
            @Parameter(description = "Sales Order ID", required = true, example = "1")
            @PathVariable Long salesOrderId) {
        return inventoryReservationService.processOrderReservation(salesOrderId);
    }
}
