package com.freshtrack.freshtrack.waste;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.freshtrack.freshtrack.batch.Batch;
import com.freshtrack.freshtrack.common.enums.WasteReason;
import com.freshtrack.freshtrack.ingredient.Ingredient;
import com.freshtrack.freshtrack.user.User;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "waste_logs")
public class WasteLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ingredient_id", nullable = false)
    private Ingredient ingredient;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "batch_id")
    private Batch batch;

    @Column(nullable = false, precision = 10, scale = 3)
    private BigDecimal quantityWasted;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private WasteReason reason;

    @Column(nullable = false)
    private Boolean alreadyDeducted;

    private String notes;

    @Column(precision = 10, scale = 2)
    private BigDecimal costAtTimeOfWaste;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "logged_by", nullable = false)
    private User loggedBy;

    @Column(nullable = false, updatable = false)
    private LocalDateTime loggedAt;

    @PrePersist
    protected void onCreate() {
        this.loggedAt = LocalDateTime.now();
    }
}