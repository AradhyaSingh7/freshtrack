package com.freshtrack.freshtrack.dish;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;


@Repository
public interface DishRepository extends JpaRepository<Dish, Long>{
    List<Dish> findByIsActiveTrue();
    @Query("""
        SELECT d FROM Dish d 
        JOIN d.dishIngredients di
        JOIN di.ingredient i
        JOIN i.batches b
        WHERE d.isActive=true
        AND b.status='EXPIRING_SOON'
        GROUP BY d
        ORDER BY COUNT(DISTINCT di.ingredient) DESC
            """)
        List<Dish> findSuggestedDishes();
}
