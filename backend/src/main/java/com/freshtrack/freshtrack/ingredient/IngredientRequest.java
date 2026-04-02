package com.freshtrack.freshtrack.ingredient;

import java.math.BigDecimal;

import com.freshtrack.freshtrack.common.enums.Unit;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class IngredientRequest {
    @NotBlank(message = "Ingredient name is required")
    private String name;

    @NotNull(message = "Unit is required")
    private Unit unit;

    @NotNull(message = "Minimum threshold is required")
    @DecimalMin(value="0.0", inclusive = false, message = "Threshold must be greater than 0")
    private BigDecimal minimumThreshold;
}
