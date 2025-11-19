package com.logistic.digitale_logistic.repository;

import com.logistic.digitale_logistic.entity.Warehouse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WareHouseRepository extends JpaRepository<Warehouse, Long> {
    boolean existsByName(String name);
}
