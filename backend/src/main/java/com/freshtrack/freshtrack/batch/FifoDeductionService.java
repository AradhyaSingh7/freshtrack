package com.freshtrack.freshtrack.batch;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.freshtrack.freshtrack.common.enums.BatchStatus;
import com.freshtrack.freshtrack.common.exception.InsufficientStockException;
import com.freshtrack.freshtrack.ingredient.Ingredient;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FifoDeductionService {
    private final BatchRepository batchRepository;

    public List<DeductionItem> deduct(Ingredient ingredient, BigDecimal quantityNeeded){
        List<Batch> availableBatches= batchRepository
        .findAvailableBatchesForIngredient(ingredient.getId());

        BigDecimal totalAvailable= availableBatches
        .stream()
        .map(Batch:: getQuantityRemaining)
        .reduce(BigDecimal.ZERO, BigDecimal::add);

        if(totalAvailable.compareTo(quantityNeeded)<0){
            throw new InsufficientStockException(
                String.format("Insufficient stock for %s. Required: %s %s Available: %s %s", ingredient.getName(), quantityNeeded, ingredient.getUnit(), totalAvailable, ingredient.getUnit())
            );
        }
        List<DeductionItem> deductions = new ArrayList<>();
        BigDecimal remaining = quantityNeeded;

        for(Batch batch : availableBatches){
        if(remaining.compareTo(BigDecimal.ZERO)==0) break;
        BigDecimal takeFromThisBatch = remaining.min(batch.getQuantityRemaining());

        batch.setQuantityRemaining(
            batch.getQuantityRemaining().subtract(takeFromThisBatch)
        );
        if(batch.getQuantityRemaining().compareTo(BigDecimal.ZERO)==0){
            batch.setStatus(BatchStatus.DEPLETED);
        }
        batchRepository.save(batch);

        deductions.add(new DeductionItem(batch, takeFromThisBatch));
        remaining= remaining.subtract(takeFromThisBatch);
    }
    return deductions;
} 
}
