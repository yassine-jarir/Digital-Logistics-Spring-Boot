package com.logistic.digitale_logistic.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ShipmentDTO {

    private Long id;
    private String shipmentNumber;
    private Long salesOrderId;
    private String salesOrderNumber;
    private String trackingNumber;
    private String carrier;
    private String status;
    private LocalDate plannedShipDate;
    private LocalDate actualShipDate;
    private LocalDateTime createdAt;
    private List<ShipmentLineDTO> lines;
}

