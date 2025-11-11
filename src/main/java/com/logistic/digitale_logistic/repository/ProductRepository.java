package com.logistic.digitale_logistic.repository;


import com.logistic.digitale_logistic.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByActiveTrue();
    Product findBySku(String sku);
}
