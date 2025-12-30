package com.logistic.digitale_logistic.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class  SalesOrderDTO {
    private Long id;
    private String orderNumber;
    private String ownerId;
    @NotNull(message = "Client ID is required")
    private Long clientId;

    @NotNull(message = "Warehouse ID is required")
    private Long warehouseId;

    @NotEmpty(message = "Order must have at least one line")
    @Valid
    private List<SoLineDTO> lines;

    private String status;
    private LocalDateTime orderDate;
    private LocalDate plannedShipDate;
    private BigDecimal totalAmount;
    private LocalDateTime updatedAt;

    // For response - include additional details
    private String clientName;
    private String warehouseName;
}

