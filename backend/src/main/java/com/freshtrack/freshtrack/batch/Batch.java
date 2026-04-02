package com.freshtrack.freshtrack.batch;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import com.freshtrack.freshtrack.common.enums.BatchStatus;
import com.freshtrack.freshtrack.ingredient.Ingredient;
import com.freshtrack.freshtrack.supplier.Supplier;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name="batches")
public class Batch {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="ingredient_id", nullable = false)
    private Ingredient ingredient;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="supplier_id", nullable = false)
    private Supplier supplier;

    @Column(nullable = false, precision = 10, scale = 3)
    private BigDecimal quantityOriginal;

    @Column(nullable = false, precision = 10, scale = 3)
    private BigDecimal quantityRemaining;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal costPerUnit;

    @Column(nullable = false)
    private LocalDate expiryDate;

    @Column(nullable = false)
    private LocalDate receivedDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BatchStatus status;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        if (this.status == null) {
            this.status = BatchStatus.ACTIVE;
        }
    }
}
