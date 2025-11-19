package com.logistic.digitale_logistic.repository;

import com.logistic.digitale_logistic.entity.Backorder;
import com.logistic.digitale_logistic.enums.BackorderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BackorderRepository extends JpaRepository<Backorder, Long> {

    List<Backorder> findBySoLine_Id(Long soLineId);

    List<Backorder> findByStatus(BackorderStatus status);

    @Query("SELECT b FROM Backorder b WHERE b.product.id = :productId AND b.warehouse.id = :warehouseId AND b.status IN ('PENDING', 'PARTIALLY_FULFILLED') ORDER BY b.createdAt ASC")
    List<Backorder> findPendingBackordersByProductAndWarehouse(@Param("productId") Long productId, @Param("warehouseId") Long warehouseId);

    @Query("SELECT b FROM Backorder b WHERE b.soLine.salesOrder.id = :salesOrderId")
    List<Backorder> findBySalesOrderId(@Param("salesOrderId") Long salesOrderId);

    @Query("SELECT COALESCE(SUM(b.quantityBackordered - b.quantityFulfilled), 0) FROM Backorder b WHERE b.product.id = :productId AND b.warehouse.id = :warehouseId AND b.status IN ('PENDING', 'PARTIALLY_FULFILLED')")
    Integer getTotalPendingBackorderQuantity(@Param("productId") Long productId, @Param("warehouseId") Long warehouseId);
}

