package com.logistic.digitale_logistic.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "products")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String sku;

    @Column(nullable = false, length = 255)
    private String name;

    @Column( length = 255)
    private String category;

    @Column(name = "selling_price", precision = 10, scale = 2, nullable = false)
    private BigDecimal sellingPrice = BigDecimal.ZERO;

    @Column(name = "cost_price", precision = 10, scale = 2, nullable = false)
    private BigDecimal costPrice = BigDecimal.ZERO;

    @Column(nullable = false)
    private Boolean active = true;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    @PrePersist
    public void generateSku() {
        if (this.sku == null || this.sku.isEmpty()) {
            // Take first 3 letters of the name in uppercase, remove spaces
            String namePart = (this.name != null && this.name.length() >= 3)
                    ? this.name.substring(0, 3).toUpperCase().replaceAll("\\s+", "")
                    : "PRD";
            // Generate a random 4-digit number
            int randomNum = (int) (Math.random() * 9000) + 1000;
            this.sku = "PROD-" + namePart + "-" + randomNum;
        }
    }

}
