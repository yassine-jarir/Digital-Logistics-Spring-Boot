package com.logistic.digitale_logistic.repository;

import com.logistic.digitale_logistic.entity.PurchaseOrder;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PurchaseOrderRepository extends JpaRepository<PurchaseOrder, Long> {
    List<PurchaseOrder> findByStatus(String status);
    List<PurchaseOrder> findByWarehouseId(Long warehouseId);
}

