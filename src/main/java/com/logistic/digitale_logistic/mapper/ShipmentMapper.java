package com.logistic.digitale_logistic.mapper;

import com.logistic.digitale_logistic.dto.ShipmentDTO;
import com.logistic.digitale_logistic.entity.Shipment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {ShipmentLineMapper.class})
public interface ShipmentMapper {

    @Mapping(source = "salesOrder.id", target = "salesOrderId")
    @Mapping(source = "salesOrder.orderNumber", target = "salesOrderNumber")
    ShipmentDTO toDTO(Shipment shipment);
}

