package com.logistic.digitale_logistic.mapper;

import com.logistic.digitale_logistic.dto.ShipmentLineDTO;
import com.logistic.digitale_logistic.entity.ShipmentLine;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ShipmentLineMapper {

    @Mapping(source = "product.id", target = "productId")
    @Mapping(source = "product.name", target = "productName")
    @Mapping(source = "product.sku", target = "productSku")
    @Mapping(source = "salesOrderLine.id", target = "salesOrderLineId")
    @Mapping(source = "quantityShipped", target = "quantityShipped")
    ShipmentLineDTO toDTO(ShipmentLine shipmentLine);
}
