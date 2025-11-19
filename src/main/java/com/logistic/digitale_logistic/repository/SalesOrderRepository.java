package com.logistic.digitale_logistic.repository;

import com.logistic.digitale_logistic.entity.SalesOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SalesOrderRepository extends JpaRepository<SalesOrder, Long> {
    List<SalesOrder> findByClientUserId(Long clientId);

}

