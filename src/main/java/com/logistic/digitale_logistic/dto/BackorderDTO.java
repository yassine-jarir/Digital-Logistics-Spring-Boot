package com.logistic.digitale_logistic.dto;

import com.logistic.digitale_logistic.enums.BackorderStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BackorderDTO {
    private Long id;
    private Long soLineId;
    private Long productId;
    private String productName;
    private String productSku;
    private Long warehouseId;
    private String warehouseName;
    private Integer quantityBackordered;
    private Integer quantityFulfilled;
    private BackorderStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime fulfilledAt;
    private Long triggeredPurchaseOrderId;
    private String notes;
}

