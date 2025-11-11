package com.logistic.digitale_logistic.dto;

import lombok.*;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductInventoryDTO {
    private Long productId;
    private String sku;
    private String name;
    private String category;
    private BigDecimal unitPrice;
    private Integer qtyOnHand;
    private Integer qtyReserved;
    private Integer qtyAvailable; // calculated: qtyOnHand - qtyReserved
}

