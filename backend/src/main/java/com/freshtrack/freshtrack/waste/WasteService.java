package com.freshtrack.freshtrack.waste;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.freshtrack.freshtrack.batch.Batch;
import com.freshtrack.freshtrack.batch.BatchRepository;
import com.freshtrack.freshtrack.batch.DeductionItem;
import com.freshtrack.freshtrack.batch.FifoDeductionService;
import com.freshtrack.freshtrack.common.enums.WasteReason;
import com.freshtrack.freshtrack.common.exception.ResourceNotFoundException;
import com.freshtrack.freshtrack.ingredient.Ingredient;
import com.freshtrack.freshtrack.ingredient.IngredientRepository;
import com.freshtrack.freshtrack.user.User;
import com.freshtrack.freshtrack.user.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class WasteService {
    private final WasteLogRepository wasteLogRepository;
    private final IngredientRepository ingredientRepository;
    private final BatchRepository batchRepository;
    private final FifoDeductionService fifoDeductionService;
    private final UserRepository userRepository;

    @Transactional
    public WasteResponse logWaste(WasteRequest request){
        Ingredient ingredient = ingredientRepository.findById(request.getIngredientId())
        .orElseThrow(()->new ResourceNotFoundException("Ingredient not found with exception: "+ request.getIngredientId()));

        User currentUser = getCurrentUser();

        Batch deductedBatch = null;
        BigDecimal costAtTimeOfWaste = BigDecimal.ZERO;

        if(!request.getAlreadyDeducted()){
            List<DeductionItem> deductions= fifoDeductionService.deduct(ingredient, request.getQuantity());

            deductedBatch = deductions.get(0).getBatch();

            costAtTimeOfWaste= deductions.stream().map(d->d.getQuantityDeducted().multiply(d.getBatch().getCostPerUnit())).reduce(BigDecimal.ZERO, BigDecimal::add);
        }
        else{
            List<Batch> recentBatches = batchRepository.findAvailableBatchesForIngredient(ingredient.getId());
            if (!recentBatches.isEmpty()) {
                deductedBatch = recentBatches.get(0);
                costAtTimeOfWaste = request.getQuantity()
                        .multiply(deductedBatch.getCostPerUnit());
            }
        }
        WasteLog wasteLog = WasteLog.builder()
                .ingredient(ingredient)
                .batch(deductedBatch)
                .quantityWasted(request.getQuantity())
                .reason(request.getReason())
                .alreadyDeducted(request.getAlreadyDeducted())
                .notes(request.getNotes())
                .costAtTimeOfWaste(costAtTimeOfWaste)
                .loggedBy(currentUser)
                .build();

        WasteLog saved = wasteLogRepository.save(wasteLog);
        return toResponse(saved);
    }
    public List<WasteResponse> getAll() {
        return wasteLogRepository.findAllByOrderByLoggedAtDesc()
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }
     public List<WasteAnalyticsResponse> getAnalytics(
            LocalDateTime start, LocalDateTime end) {

        return wasteLogRepository.getWasteAnalyticsByReason(start, end)
                .stream()
                .map(row -> WasteAnalyticsResponse.builder()
                        .reason((WasteReason) row[0])
                        .count((Long) row[1])
                        .totalCost(row[2] != null
                            ? (BigDecimal) row[2]
                            : BigDecimal.ZERO)
                        .build())
                .collect(Collectors.toList());
    }
     private User getCurrentUser() {
        String email = SecurityContextHolder.getContext()
                .getAuthentication().getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException(
                    "Current user not found"));
    }

    private WasteResponse toResponse(WasteLog log) {
        return WasteResponse.builder()
                .id(log.getId())
                .ingredientId(log.getIngredient().getId())
                .ingredientName(log.getIngredient().getName())
                .quantityWasted(log.getQuantityWasted())
                .reason(log.getReason())
                .alreadyDeducted(log.getAlreadyDeducted())
                .notes(log.getNotes())
                .costAtTimeOfWaste(log.getCostAtTimeOfWaste())
                .loggedBy(log.getLoggedBy().getName())
                .loggedAt(log.getLoggedAt())
                .build();
    }
}
