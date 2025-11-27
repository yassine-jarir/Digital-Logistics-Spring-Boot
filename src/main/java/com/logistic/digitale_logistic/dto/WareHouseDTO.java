package com.logistic.digitale_logistic.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WareHouseDTO {
    private Long id;
    private String name;
    private String location;
    private Boolean active;

    public WareHouseDTO(String newName, String newLocation, boolean b) {
    }
}

