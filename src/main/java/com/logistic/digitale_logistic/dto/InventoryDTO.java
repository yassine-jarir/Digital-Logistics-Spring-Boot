package com.logistic.digitale_logistic.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InventoryDTO {

    private Long id;
    private Long productId;
    private Long warehouseId;
    private Integer qtyOnHand;
    private Integer qtyReserved;
    private Integer qtyAvailable;
}
