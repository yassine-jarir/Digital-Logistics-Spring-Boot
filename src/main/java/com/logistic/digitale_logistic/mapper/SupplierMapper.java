package com.logistic.digitale_logistic.mapper;

import com.logistic.digitale_logistic.dto.SupplierDTO;
import com.logistic.digitale_logistic.entity.Supplier;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface SupplierMapper {
    SupplierDTO toDTO(Supplier supplier);
    Supplier toEntity(SupplierDTO supplierDTO);
}

