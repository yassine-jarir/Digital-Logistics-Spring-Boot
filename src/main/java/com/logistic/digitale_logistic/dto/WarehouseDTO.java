package com.logistic.digitale_logistic.dto;

import lombok.*;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WarehouseDTO {
    private Long id;
    private String name;
    private String location;
    private Boolean active;
    private List<ProductInventoryDTO> products;
}

