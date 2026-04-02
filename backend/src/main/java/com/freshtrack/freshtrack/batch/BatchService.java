package com.freshtrack.freshtrack.batch;


import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.freshtrack.freshtrack.common.enums.BatchStatus;
import com.freshtrack.freshtrack.common.exception.ResourceNotFoundException;
import com.freshtrack.freshtrack.ingredient.Ingredient;
import com.freshtrack.freshtrack.ingredient.IngredientRepository;
import com.freshtrack.freshtrack.supplier.Supplier;
import com.freshtrack.freshtrack.supplier.SupplierRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BatchService {
    private final IngredientRepository ingredientRepository;
    private final SupplierRepository supplierRepository;
    private final BatchRepository batchRepository;

    public BatchResponse create(BatchRequest request){
        Ingredient ingredient = ingredientRepository.findById(request.getIngredientId())
        .orElseThrow(()->new ResourceNotFoundException("Ingredient not found with id: "+request.getIngredientId()));

        Supplier supplier = supplierRepository.findById(request.getSupplierId())
        .orElseThrow(()->new ResourceNotFoundException("Supplier not found with id: "+request.getIngredientId()));

        BatchStatus initialStatus = determineStatus(request.getExpiryDate());

        Batch batch = Batch.builder()
        .ingredient(ingredient)
        .supplier(supplier)
        .quantityOriginal(request.getQuantity())
        .quantityRemaining(request.getQuantity())
        .costPerUnit(request.getCostPerUnit())
        .expiryDate(request.getExpiryDate())
        .receivedDate(request.getReceivedDate())
        .status(initialStatus)
        .build();

        Batch saved = batchRepository.save(batch);
        return toResponse(saved);
    }

    public List<BatchResponse> getAll(){
        return batchRepository.findAll()
        .stream()
        .map(this:: toResponse)
        .collect(Collectors.toList());
    }

    public List<BatchResponse> getByIngredient(Long ingredientId){
         if (!ingredientRepository.existsById(ingredientId)) {
            throw new ResourceNotFoundException(
                "Ingredient not found with id: " + ingredientId);
        }
        return batchRepository
        .findAvailableBatchesForIngredient(ingredientId)
        .stream()
        .map(this::toResponse)
        .collect(Collectors.toList());
    }

    public List<BatchResponse> getExpiringSoon(){
        return batchRepository.findByStatus(BatchStatus.EXPIRING_SOON)
        .stream()
        .map(this::toResponse)
        .collect(Collectors.toList());
    }

    public void updateExpiredBatches(){
        List<Batch> activeBatches= batchRepository.findByStatus(BatchStatus.ACTIVE);
        List<Batch> expiringSoonBatches = batchRepository.findByStatus(BatchStatus.EXPIRING_SOON);

        activeBatches.forEach(batch->{
            BatchStatus newStatus= determineStatus(batch.getExpiryDate());
            if(newStatus!=BatchStatus.ACTIVE){
                batch.setStatus(newStatus);
                if(newStatus==BatchStatus.EXPIRED){
                    batch.setQuantityRemaining(java.math.BigDecimal.ZERO);
                }
            }       
        });
        expiringSoonBatches.forEach(batch -> {
            if (batch.getExpiryDate().isBefore(LocalDate.now())) {
                batch.setStatus(BatchStatus.EXPIRED);
                batch.setQuantityRemaining(java.math.BigDecimal.ZERO);
            }
        });
        batchRepository.saveAll(activeBatches);
        batchRepository.saveAll(expiringSoonBatches);
    }
     private BatchStatus determineStatus(LocalDate expiryDate) {
        LocalDate today = LocalDate.now();
        LocalDate twoDaysFromNow = today.plusDays(2);

        if (expiryDate.isBefore(today)) {
            return BatchStatus.EXPIRED;
        } else if (!expiryDate.isAfter(twoDaysFromNow)) {
            return BatchStatus.EXPIRING_SOON;
        } else {
            return BatchStatus.ACTIVE;
        }
    }

    public BatchResponse toResponse(Batch batch) {
        return BatchResponse.builder()
                .id(batch.getId())
                .ingredientId(batch.getIngredient().getId())
                .ingredientName(batch.getIngredient().getName())
                .supplierId(batch.getSupplier().getId())
                .supplierName(batch.getSupplier().getName())
                .quantityOriginal(batch.getQuantityOriginal())
                .quantityRemaining(batch.getQuantityRemaining())
                .costPerUnit(batch.getCostPerUnit())
                .expiryDate(batch.getExpiryDate())
                .receivedDate(batch.getReceivedDate())
                .status(batch.getStatus())
                .createdAt(batch.getCreatedAt())
                .build();
    }
}
