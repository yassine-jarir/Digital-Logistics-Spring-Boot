package com.logistic.digitale_logistic.mapper;

import com.logistic.digitale_logistic.dto.SoLineDTO;
import com.logistic.digitale_logistic.entity.SoLine;
import org.springframework.stereotype.Component;

@Component
public class SoLineMapper {

    public SoLineDTO toDTO(SoLine soLine) {
        if (soLine == null) {
            return null;
        }

        return SoLineDTO.builder()
                .id(soLine.getId())
                .productId(soLine.getProduct().getId())
                .productName(soLine.getProduct().getName())
                .productSku(soLine.getProduct().getSku())
                .quantity(soLine.getOrderedQuantity())
                .unitPrice(soLine.getUnitPrice())
                .lineTotal(soLine.getLineTotal())
                .reservedQuantity(soLine.getReservedQuantity())
                .build();
    }
}

