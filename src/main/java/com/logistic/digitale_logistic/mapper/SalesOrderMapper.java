package com.logistic.digitale_logistic.mapper;

import com.logistic.digitale_logistic.dto.SalesOrderDTO;
import com.logistic.digitale_logistic.entity.SalesOrder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class SalesOrderMapper {

    private final SoLineMapper soLineMapper;

    public SalesOrderDTO toDTO(SalesOrder salesOrder) {
        if (salesOrder == null) {
            return null;
        }

        return SalesOrderDTO.builder()
                .id(salesOrder.getId())
                .orderNumber(salesOrder.getOrderNumber())
                .clientId(salesOrder.getClient().getUserId())
                .clientName(salesOrder.getClient().getUser().getName())
                .warehouseId(salesOrder.getWarehouse().getId())
                .warehouseName(salesOrder.getWarehouse().getName())
                .status(salesOrder.getStatus())
                .orderDate(salesOrder.getOrderDate())
                .plannedShipDate(salesOrder.getPlannedShipDate())
                .totalAmount(salesOrder.getTotalAmount())
                .updatedAt(salesOrder.getUpdatedAt())
                .lines(salesOrder.getLines().stream()
                        .map(soLineMapper::toDTO)
                        .collect(Collectors.toList()))
                .build();
    }
}
