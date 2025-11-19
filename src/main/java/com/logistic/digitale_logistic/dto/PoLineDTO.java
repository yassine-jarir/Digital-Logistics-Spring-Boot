package com.logistic.digitale_logistic.dto;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PoLineDTO {
    private Long id;
    private Long productId;
    private String productName;
    private Integer orderedQuantity;
    private Integer receivedQuantity;
    private BigDecimal unitCost;
}

