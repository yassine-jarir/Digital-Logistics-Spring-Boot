package com.logistic.digitale_logistic.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReservationResultDTO {

    private Long salesOrderId;
    private String salesOrderNumber;
    private String status;
    private boolean fullyReserved;
    private boolean hasBackorders;
    private List<BackorderDTO> backorders;
    private String message;
}
