package com.logistic.digitale_logistic.repository;


import com.logistic.digitale_logistic.entity.Inventory;
import com.logistic.digitale_logistic.entity.Warehouse;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InventoryRepository extends JpaRepository<Inventory, Long> {


    List<Inventory> findByWarehouse(Warehouse warehouse);
}
