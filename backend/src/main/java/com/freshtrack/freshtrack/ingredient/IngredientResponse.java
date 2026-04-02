package com.freshtrack.freshtrack.ingredient;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.freshtrack.freshtrack.common.enums.Unit;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class IngredientResponse {
    private Long id;
    private String name;
    private Unit unit;
    private BigDecimal minimumThreshold;
    private BigDecimal availableQuantity;
    private LocalDateTime createdAt;
}