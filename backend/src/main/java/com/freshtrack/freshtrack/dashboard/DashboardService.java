package com.freshtrack.freshtrack.dashboard;

import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import java.util.List;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import com.freshtrack.freshtrack.batch.BatchRepository;
import com.freshtrack.freshtrack.batch.BatchService;
import com.freshtrack.freshtrack.common.enums.BatchStatus;
import com.freshtrack.freshtrack.ingredient.IngredientService;
import com.freshtrack.freshtrack.ingredient.IngredientResponse;
import com.freshtrack.freshtrack.dish.DishService;
import com.freshtrack.freshtrack.dish.DishResponse;
import com.freshtrack.freshtrack.waste.WasteLogRepository;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final BatchRepository batchRepository;
    private final BatchService batchService;
    private final IngredientService ingredientService;
    private final DishService dishService;
    private final WasteLogRepository wasteLogRepository;

    public DashboardResponse getDashboard() {

        List<IngredientResponse> lowStock = ingredientService.getLowStock();

        var expiringSoonBatches = batchRepository
                .findByStatus(BatchStatus.EXPIRING_SOON)
                .stream()
                .map(batchService::toResponse)
                .toList();

        List<DishResponse> suggestions = dishService.getSuggested();

        BigDecimal wasteCostThisWeek = getWasteCostThisWeek();

        return DashboardResponse.builder()
                .totalIngredients(
                    (long) ingredientService.getAll().size())
                .expiringSoonCount((long) expiringSoonBatches.size())
                .lowStockCount((long) lowStock.size())
                .totalWasteCostThisWeek(wasteCostThisWeek)
                .expiringSoonBatches(expiringSoonBatches)
                .lowStockIngredients(lowStock)
                .suggestedDishes(suggestions)
                .build();
    }

    private BigDecimal getWasteCostThisWeek() {
        LocalDateTime weekStart = LocalDateTime.now().minusDays(7);
        LocalDateTime now = LocalDateTime.now();

        return wasteLogRepository
                .getWasteAnalyticsByReason(weekStart, now)
                .stream()
                .map(row -> row[2] != null
                    ? (BigDecimal) row[2]
                    : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}