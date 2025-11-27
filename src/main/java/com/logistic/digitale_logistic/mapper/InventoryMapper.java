package com.logistic.digitale_logistic.mapper;

import com.logistic.digitale_logistic.dto.InventoryDTO;
import com.logistic.digitale_logistic.entity.Inventory;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface InventoryMapper {
     InventoryDTO toDTO(Inventory inventory);
    Inventory toEntity(InventoryDTO inventoryDTO);
//    getWarehouseId


}
