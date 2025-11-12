package com.logistic.digitale_logistic.controller.Client;

import com.logistic.digitale_logistic.dto.ReservationRequestDTO;
import com.logistic.digitale_logistic.dto.ReservationResultDTO;
import com.logistic.digitale_logistic.service.client.InventoryReservationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/client/reservations")
@RequiredArgsConstructor
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
    @PostMapping("/{salesOrderId}/process")
    @PreAuthorize("hasRole('CLIENT')")
    @ResponseStatus(HttpStatus.OK)
    public ReservationResultDTO processReservationById(@PathVariable Long salesOrderId) {
        return inventoryReservationService.processOrderReservation(salesOrderId);
    }
}
