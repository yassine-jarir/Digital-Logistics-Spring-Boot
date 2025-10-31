package com.logistic.digitale_logistic.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "inventory", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"product_id", "warehouse_id"})
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Inventory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "product_id", nullable = false)
    @ToString.Exclude
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "warehouse_id", nullable = false)
    @ToString.Exclude
    private Warehouse warehouse;

    @Column(name = "qty_on_hand", nullable = false)
    private Integer qtyOnHand = 0;

    @Column(name = "qty_reserved", nullable = false)
    private Integer qtyReserved = 0;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Transient
    public Integer getQtyAvailable() {
        if (qtyOnHand == null) return null;
        if (qtyReserved == null) return qtyOnHand;
        return qtyOnHand - qtyReserved;
    }
}
