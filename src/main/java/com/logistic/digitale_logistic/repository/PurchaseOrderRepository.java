package com.logistic.digitale_logistic.repository;

import com.logistic.digitale_logistic.entity.PurchaseOrder;
import com.logistic.digitale_logistic.enums.PurchaseOrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PurchaseOrderRepository extends JpaRepository<PurchaseOrder, Long> {
    List<PurchaseOrder> findByStatus(PurchaseOrderStatus status);
    List<PurchaseOrder> findByWarehouseId(Long warehouseId);
}
