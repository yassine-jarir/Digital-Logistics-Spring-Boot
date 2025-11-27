package com.logistic.digitale_logistic.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ShipmentLineDTO {

    private Long id;
    private Long productId;
    private String productName;
    private String productSku;
    private Integer quantity;
    private Long salesOrderLineId;
    private Integer quantityShipped;
}
