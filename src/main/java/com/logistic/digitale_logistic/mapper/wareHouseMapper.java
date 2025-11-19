package com.logistic.digitale_logistic.mapper;

import com.logistic.digitale_logistic.dto.WareHouseDTO;
import com.logistic.digitale_logistic.entity.Warehouse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface wareHouseMapper {
    WareHouseDTO toDto(Warehouse warehouse);
    Warehouse toEntity(WareHouseDTO dto);
}
