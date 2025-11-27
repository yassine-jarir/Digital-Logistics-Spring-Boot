package com.logistic.digitale_logistic.repository;

import com.logistic.digitale_logistic.entity.InventoryMovement;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InventoryMovementRepository extends JpaRepository<InventoryMovement, Long> {
}
