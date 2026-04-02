package com.freshtrack.freshtrack.dish;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class DishResponse {
    private Long id;
    private String name;
    private String description;
    private Boolean isActive;
    private List<RecipeItemResponse> recipe;
    private LocalDateTime createdAt;

    @Data
    @Builder
    public static class RecipeItemResponse {
        private Long ingredientId;
        private String ingredientName;
        private BigDecimal quantityRequired;
        private String unit;
    }
}