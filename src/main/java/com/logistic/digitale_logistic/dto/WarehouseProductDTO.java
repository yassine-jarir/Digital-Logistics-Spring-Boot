package com.logistic.digitale_logistic.dto;

import lombok.*;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WarehouseProductDTO {
    private Long productId;
    private String name;
    private String sku;
    private String category;
    private BigDecimal unitPrice; // This is the selling price
    private Integer availableQuantity; // qtyOnHand - qtyReserved
    private Integer qtyOnHand;
    private Integer qtyReserved;
}

