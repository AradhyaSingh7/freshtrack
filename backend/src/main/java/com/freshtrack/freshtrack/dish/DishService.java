package com.freshtrack.freshtrack.dish;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.freshtrack.freshtrack.common.exception.ResourceNotFoundException;
import com.freshtrack.freshtrack.ingredient.Ingredient;
import com.freshtrack.freshtrack.ingredient.IngredientRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DishService {
    private final DishRepository dishRepository;
    private final DishIngredientRepository dishIngredientRepository;
    private final IngredientRepository ingredientRepository;

    @Transactional
    public DishResponse create(DishRequest request){
        Dish dish = Dish.builder()
        .name(request.getName())
        .description(request.getDescription())
        .build();

        Dish savedDish= dishRepository.save(dish);

        List<DishIngredient> dishIngredients= request.getIngredients()
        .stream()
        .map(ingredientRequest ->{
            Ingredient ingredient = ingredientRepository.findById(ingredientRequest.getIngredientId())
            .orElseThrow(()->new ResourceNotFoundException("Ingredient not found: "+ingredientRequest.getIngredientId()));

            return DishIngredient.builder()
            .dish(savedDish)
            .ingredient(ingredient)
            .quantityRequired(ingredientRequest.getQuantityRequired())
            .build();
        })
        .collect(Collectors.toList());
        dishIngredientRepository.saveAll(dishIngredients);
        savedDish.setDishIngredients(dishIngredients);
        return toResponse(savedDish);
    }

    public List<DishResponse> getActive(){
        return dishRepository.findByIsActiveTrue()
        .stream().map(this::toResponse)
        .collect(Collectors.toList());
    }
    public List<DishResponse> getSuggested() {
        return dishRepository.findSuggestedDishes()
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }
    public List<DishResponse> getAll() {
        return dishRepository.findAll()
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }
    private DishResponse toResponse(Dish dish) {
        List<DishResponse.RecipeItemResponse> recipe = dish.getDishIngredients()
                .stream()
                .map(di -> DishResponse.RecipeItemResponse.builder()
                        .ingredientId(di.getIngredient().getId())
                        .ingredientName(di.getIngredient().getName())
                        .quantityRequired(di.getQuantityRequired())
                        .unit(di.getIngredient().getUnit().name())
                        .build())
                .collect(Collectors.toList());

        return DishResponse.builder()
                .id(dish.getId())
                .name(dish.getName())
                .description(dish.getDescription())
                .isActive(dish.getIsActive())
                .recipe(recipe)
                .createdAt(dish.getCreatedAt())
                .build();
    }
}
