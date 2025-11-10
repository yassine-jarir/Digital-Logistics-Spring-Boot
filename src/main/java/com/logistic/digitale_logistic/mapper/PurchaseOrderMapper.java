package com.logistic.digitale_logistic.mapper;
import com.logistic.digitale_logistic.dto.PoLineDTO;
import com.logistic.digitale_logistic.dto.PurchaseOrderDTO;
import com.logistic.digitale_logistic.entity.PoLine;
import com.logistic.digitale_logistic.entity.PurchaseOrder;
import com.logistic.digitale_logistic.enums.PurchaseOrderStatus;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface PurchaseOrderMapper {
    @Mapping(source = "supplier.id", target = "supplierId")
    @Mapping(source = "supplier.name", target = "supplierName")
    @Mapping(source = "warehouse.id", target = "warehouseId")
    @Mapping(source = "warehouse.name", target = "warehouseName")
    @Mapping(source = "status", target = "status", qualifiedByName = "statusToString")
    PurchaseOrderDTO toDTO(PurchaseOrder purchaseOrder);
    
    @Mapping(source = "product.id", target = "productId")
    @Mapping(source = "product.name", target = "productName")
    PoLineDTO toLineDTO(PoLine poLine);
    
    @Named("statusToString")
    default String statusToString(PurchaseOrderStatus status) {
        return status != null ? status.name() : null;
    }
}
