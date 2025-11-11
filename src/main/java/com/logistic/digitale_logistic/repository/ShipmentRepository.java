package com.logistic.digitale_logistic.repository;

import com.logistic.digitale_logistic.entity.Shipment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ShipmentRepository extends JpaRepository<Shipment, Long> {

    List<Shipment> findBySalesOrder_Id(Long salesOrderId);

    List<Shipment> findByStatus(String status);
}

