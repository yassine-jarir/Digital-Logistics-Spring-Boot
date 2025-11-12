package com.logistic.digitale_logistic.repository;


import com.logistic.digitale_logistic.entity.Inventory;
import com.logistic.digitale_logistic.entity.Warehouse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface InventoryRepository extends JpaRepository<Inventory, Long> {

    List<Inventory> findByWarehouse(Warehouse warehouse);

    Optional<Inventory> findByProduct_IdAndWarehouse_Id(Long productId, Long warehouseId);

    @Query("SELECT i FROM Inventory i WHERE i.product.sku = :sku")
    Optional<Inventory> findByProductSku(@Param("sku") String sku);

    @Query("SELECT i FROM Inventory i WHERE i.product.id = :productId ORDER BY i.qtyAvailable DESC")
    List<Inventory> findAllByProductIdOrderByQtyAvailableDesc(@Param("productId") Long productId);

    @Query("SELECT COALESCE(SUM(i.qtyReserved), 0) FROM Inventory i WHERE i.product.id = :productId")
    Integer findTotalQtyReservedByProductId(@Param("productId") Long productId);
}
