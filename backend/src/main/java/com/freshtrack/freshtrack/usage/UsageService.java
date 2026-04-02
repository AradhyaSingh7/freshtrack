package com.freshtrack.freshtrack.usage;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.freshtrack.freshtrack.batch.Batch;
import com.freshtrack.freshtrack.batch.BatchRepository;
import com.freshtrack.freshtrack.batch.DeductionItem;
import com.freshtrack.freshtrack.batch.FifoDeductionService;
import com.freshtrack.freshtrack.common.exception.InsufficientStockException;
import com.freshtrack.freshtrack.common.exception.ResourceNotFoundException;
import com.freshtrack.freshtrack.dish.DishRepository;
import com.freshtrack.freshtrack.user.User;
import com.freshtrack.freshtrack.user.UserRepository;
import com.freshtrack.freshtrack.dish.Dish;
import com.freshtrack.freshtrack.dish.DishIngredient;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UsageService {
    private final UsageLogRepository usageLogRepository;
    private final UsageLogItemRepository usageLogItemRepository;
    private final DishRepository dishRepository;
    private final UserRepository userRepository;
    private final FifoDeductionService fifoDeductionService;
    private final BatchRepository batchRepository;
    @Transactional
    public UsageResponse logUsage(UsageRequest request){
        Dish dish = dishRepository.findById(request.getDishId())
                    .orElseThrow(()-> new ResourceNotFoundException("Dish not found with id: "+request.getDishId()));

        User currentUser = getCurrentUser();

        UsageLog usageLog= UsageLog.builder()
        .dish(dish)
        .portionsPrepared(request.getPortions())
        .loggedBy(currentUser)
        .build();

        UsageLog savedLog= usageLogRepository.save(usageLog);
        List<UsageLogItem> auditItems = new ArrayList<>();

        for(DishIngredient dishIngredient: dish.getDishIngredients()){
            BigDecimal totalNeeded = dishIngredient.getQuantityRequired().multiply(BigDecimal.valueOf(request.getPortions()));

            List<DeductionItem> deductions = fifoDeductionService.deduct(dishIngredient.getIngredient(), totalNeeded);

            for(DeductionItem deduction: deductions){
                UsageLogItem item = UsageLogItem.builder()
                .usageLog(savedLog)
                .batch(deduction.getBatch())
                .ingredient(dishIngredient.getIngredient())
                .quantityDeducted(deduction.getQuantityDeducted())
                .build();
                auditItems.add(item);
            }
        }
        usageLogItemRepository.saveAll(auditItems);
        return toResponse(savedLog, auditItems);   
    }
    public List<UsageResponse> getHistory() {
        return usageLogRepository.findAllByOrderByLoggedAtDesc()
                .stream()
                .map(log -> toResponse(log, log.getItems()))
                .collect(Collectors.toList());
    }
    private User getCurrentUser() {
        String email = SecurityContextHolder.getContext()
                .getAuthentication().getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException(
                    "Current user not found"));
    }
    public UsageResponse previewUsage(UsageRequest request) {
    Dish dish = dishRepository.findById(request.getDishId())
            .orElseThrow(() -> new ResourceNotFoundException(
                "Dish not found with id: " + request.getDishId()));

    List<UsageLogItem> previewItems = new ArrayList<>();

    for (DishIngredient dishIngredient : dish.getDishIngredients()) {
        BigDecimal totalNeeded = dishIngredient.getQuantityRequired()
                .multiply(BigDecimal.valueOf(request.getPortions()));

        List<Batch> batches = batchRepository
                .findAvailableBatchesForIngredient(
                    dishIngredient.getIngredient().getId());

        BigDecimal totalAvailable = batches.stream()
                .map(Batch::getQuantityRemaining)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        if (totalAvailable.compareTo(totalNeeded) < 0) {
            throw new InsufficientStockException(
                String.format(
                    "Insufficient stock for %s. Required: %s, Available: %s",
                    dishIngredient.getIngredient().getName(),
                    totalNeeded,
                    totalAvailable));
        }

        BigDecimal remaining = totalNeeded;
        for (Batch batch : batches) {
            if (remaining.compareTo(BigDecimal.ZERO) == 0) break;
            BigDecimal take = remaining.min(batch.getQuantityRemaining());

            UsageLogItem previewItem = UsageLogItem.builder()
                    .batch(batch)
                    .ingredient(dishIngredient.getIngredient())
                    .quantityDeducted(take)
                    .build();
            previewItems.add(previewItem);
            remaining = remaining.subtract(take);
        }
    }

    UsageLog previewLog = UsageLog.builder()
            .dish(dish)
            .portionsPrepared(request.getPortions())
            .loggedBy(getCurrentUser())
            .build();

    return toResponse(previewLog, previewItems);
}
    private UsageResponse toResponse(UsageLog log, List<UsageLogItem> items) {
        List<UsageItemResponse> deductions = items.stream()
                .map(item -> UsageItemResponse.builder()
                        .batchId(item.getBatch().getId())
                        .ingredientName(item.getIngredient().getName())
                        .quantityDeducted(item.getQuantityDeducted())
                        .batchExpiryDate(item.getBatch().getExpiryDate())
                        .build())
                .collect(Collectors.toList());

        return UsageResponse.builder()
                .id(log.getId())
                .dishId(log.getDish().getId())
                .dishName(log.getDish().getName())
                .portionsPrepared(log.getPortionsPrepared())
                .loggedBy(log.getLoggedBy() != null 
    ? log.getLoggedBy().getName() 
    : "PREVIEW")
                .loggedAt(log.getLoggedAt())
                .deductions(deductions)
                .build();
    }
}
