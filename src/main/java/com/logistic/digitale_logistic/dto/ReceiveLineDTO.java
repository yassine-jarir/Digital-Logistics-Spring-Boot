package com.logistic.digitale_logistic.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReceiveLineDTO {
    private Long poLineId;
    private Integer receivedQuantity;
}

