package com.freshtrack.freshtrack.dish;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import java.util.List;

@Data
public class DishRequest {

    @NotBlank(message = "Dish name is required")
    private String name;

    private String description;

    @NotEmpty(message = "A dish must have at least one ingredient")
    private List<DishIngredientRequest> ingredients;
}
