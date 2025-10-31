package com.logistic.digitale_logistic.mapper;


import com.logistic.digitale_logistic.entity.Warehouse;
import org.mapstruct.Mapper;

@Mapper(componentModel= "spring")
public interface wareHouseMapper {
    wareHouseDTO toDto(Warehouse wareHouse);
    Warehouse toEntity(wareHouseDTO dto);
}
