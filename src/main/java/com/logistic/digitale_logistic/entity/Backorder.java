package com.logistic.digitale_logistic.entity;

import com.logistic.digitale_logistic.enums.BackorderStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "backorders")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Backorder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "so_line_id", nullable = false)
    @ToString.Exclude
    private SoLine soLine;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "product_id", nullable = false)
    @ToString.Exclude
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "warehouse_id", nullable = false)
    @ToString.Exclude
    private Warehouse warehouse;

    @Column(name = "quantity_backordered", nullable = false)
    private Integer quantityBackordered;

    @Column(name = "quantity_fulfilled", nullable = false)
    private Integer quantityFulfilled = 0;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private BackorderStatus status = BackorderStatus.PENDING;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "fulfilled_at")
    private LocalDateTime fulfilledAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "triggered_po_id")
    @ToString.Exclude
    private PurchaseOrder triggeredPurchaseOrder; // Auto-generated PO to supplier

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}

