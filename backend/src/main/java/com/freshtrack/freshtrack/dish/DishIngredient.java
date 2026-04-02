package com.freshtrack.freshtrack.dish;
import java.math.BigDecimal;

import com.freshtrack.freshtrack.ingredient.Ingredient;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name="dish_ingredients")
public class DishIngredient {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="dish_id", nullable = false)
    private Dish dish;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="ingredient_id", nullable = false)
    private Ingredient ingredient;

    @Column(nullable=false, precision=10, scale=3)
    private BigDecimal quantityRequired;
}
