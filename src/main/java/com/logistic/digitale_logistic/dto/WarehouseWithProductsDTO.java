package com.logistic.digitale_logistic.dto;

import lombok.*;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WarehouseWithProductsDTO {
    private Long id;
    private String name;
    private String location;
    private Boolean active;
    private List<WarehouseProductDTO> products;
}
