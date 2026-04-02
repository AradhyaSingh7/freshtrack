package com.freshtrack.freshtrack.ingredient;


import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.freshtrack.freshtrack.batch.BatchRepository;
import com.freshtrack.freshtrack.common.exception.ResourceNotFoundException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class IngredientService {
    private final IngredientRepository ingredientRepository;
    private final BatchRepository batchRepository;


    public IngredientResponse create(IngredientRequest request){
        Ingredient ingredient= Ingredient.builder()
        .name(request.getName())
        .unit(request.getUnit())
        .minimumThreshold(request.getMinimumThreshold())
        .build();
        
        Ingredient saved = ingredientRepository.save(ingredient);
        return toResponse(saved);
    }
    public List<IngredientResponse> getAll(){
        return ingredientRepository.findAll()
        .stream()
        .map(this::toResponse)
        .collect(Collectors.toList());
    }

    public IngredientResponse getById(Long id){
        Ingredient ingredient = ingredientRepository.findById(id)
        .orElseThrow(()-> new ResourceNotFoundException("Ingredient not found with id: "+id));
        return toResponse(ingredient);
    }
    public List<IngredientResponse> getLowStock(){
        return ingredientRepository.findAll()
        .stream()
        .map(this::toResponse)
        .filter(response-> response.getAvailableQuantity().compareTo(response.getMinimumThreshold())<0)
        .collect(Collectors.toList());
    }
    
    public IngredientResponse update(Long id, IngredientRequest request){
        Ingredient ingredient = ingredientRepository.findById(id)
        .orElseThrow(()-> new ResourceNotFoundException("Ingredient not found with id: " + id));
        ingredient.setName(request.getName());
        ingredient.setUnit(request.getUnit());
        ingredient.setMinimumThreshold(request.getMinimumThreshold());

        Ingredient saved = ingredientRepository.save(ingredient);
        return toResponse(saved);
    }
    public void delete(Long id) {
        if (!ingredientRepository.existsById(id)) {
            throw new ResourceNotFoundException(
                "Ingredient not found with id: " + id);
        }
        ingredientRepository.deleteById(id);
    }
    private IngredientResponse toResponse(Ingredient ingredient){
        BigDecimal availableQuantity = batchRepository
        .findAvailableBatchesForIngredient(ingredient.getId())
        .stream()
        .map(batch->batch.getQuantityRemaining())
        .reduce(BigDecimal.ZERO, BigDecimal::add);

        return IngredientResponse.builder()
                .id(ingredient.getId())
                .name(ingredient.getName())
                .unit(ingredient.getUnit())
                .minimumThreshold(ingredient.getMinimumThreshold())
                .availableQuantity(availableQuantity)
                .createdAt(ingredient.getCreatedAt())
                .build();
    }
}
