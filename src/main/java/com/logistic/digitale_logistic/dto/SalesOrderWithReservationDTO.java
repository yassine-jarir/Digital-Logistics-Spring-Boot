package com.logistic.digitale_logistic.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Combined response DTO containing both Sales Order and Reservation Result
 * Used when creating a sales order with automatic reservation
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SalesOrderWithReservationDTO {

    /**
     * The created sales order details
     */
    private SalesOrderDTO salesOrder;

    /**
     * The reservation result with stock allocation and backorder information
     */
    private ReservationResultDTO reservationResult;
}

