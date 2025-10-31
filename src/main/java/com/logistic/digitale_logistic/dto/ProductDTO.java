package com.logistic.digitale_logistic.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class    ProductDTO {

    private Long id;

    @NotBlank
    @Size(max = 255)
    private String name;
// a
    @NotBlank
    @Size(max = 255)
    private String category;

    @NotBlank
    @NotNull
    @PositiveOrZero
    private BigDecimal sellingPrice;

    @NotBlank
    @NotNull
    @PositiveOrZero
    private BigDecimal costPrice;

    private Boolean active;
}
