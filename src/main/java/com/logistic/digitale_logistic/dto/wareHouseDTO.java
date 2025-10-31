package com.logistic.digitale_logistic.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class wareHouseDTO {
    private Long id;
    private String name;
    private String location;
    private String code;
    private Boolean active;
}
