package com.logistic.digitale_logistic.mapper;
import com.logistic.digitale_logistic.dto.PoLineDTO;
import com.logistic.digitale_logistic.dto.PurchaseOrderDTO;
import com.logistic.digitale_logistic.entity.PoLine;
import com.logistic.digitale_logistic.entity.PurchaseOrder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
@Mapper(componentModel = "spring")
public interface PurchaseOrderMapper {
    @Mapping(source = "supplier.id", target = "supplierId")
    @Mapping(source = "supplier.name", target = "supplierName")
    @Mapping(source = "warehouse.id", target = "warehouseId")
    @Mapping(source = "warehouse.name", target = "warehouseName")
    PurchaseOrderDTO toDTO(PurchaseOrder purchaseOrder);
    @Mapping(source = "product.id", target = "productId")
    @Mapping(source = "product.name", target = "productName")
    PoLineDTO toLineDTO(PoLine poLine);
}
