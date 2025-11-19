package com.logistic.digitale_logistic.mapper;

import com.logistic.digitale_logistic.dto.BackorderDTO;
import com.logistic.digitale_logistic.entity.Backorder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface BackorderMapper {

    @Mapping(source = "soLine.id", target = "soLineId")
    @Mapping(source = "product.id", target = "productId")
    @Mapping(source = "product.name", target = "productName")
    @Mapping(source = "product.sku", target = "productSku")
    @Mapping(source = "warehouse.id", target = "warehouseId")
    @Mapping(source = "warehouse.name", target = "warehouseName")
    @Mapping(source = "triggeredPurchaseOrder.id", target = "triggeredPurchaseOrderId")
    BackorderDTO toDTO(Backorder backorder);
}

