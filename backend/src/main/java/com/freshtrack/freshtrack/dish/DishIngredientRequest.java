package com.freshtrack.freshtrack.dish;


import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class DishIngredientRequest {

    @NotNull(message = "Ingredient id is required")
    private Long ingredientId;

    @NotNull(message = "Quantity required is required")
    @DecimalMin(value = "0.0", inclusive = false,
        message = "Quantity must be greater than 0")
    private BigDecimal quantityRequired;
}
