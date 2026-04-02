package com.freshtrack.freshtrack.dashboard;

import com.freshtrack.freshtrack.batch.BatchResponse;
import com.freshtrack.freshtrack.dish.DishResponse;
import com.freshtrack.freshtrack.ingredient.IngredientResponse;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
public class DashboardResponse {
    private Long totalIngredients;
    private Long expiringSoonCount;
    private Long lowStockCount;
    private BigDecimal totalWasteCostThisWeek;
    private List<BatchResponse> expiringSoonBatches;
    private List<IngredientResponse> lowStockIngredients;
    private List<DishResponse> suggestedDishes;
}