package com.logistic.digitale_logistic.repository;

import com.logistic.digitale_logistic.entity.Product;
import com.logistic.digitale_logistic.entity.SalesOrder;
import com.logistic.digitale_logistic.entity.SoLine;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SoLineRepository extends JpaRepository<SoLine, Long> {
    List<SoLine> findProductById(Long id);
}
