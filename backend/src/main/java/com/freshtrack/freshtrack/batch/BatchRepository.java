package com.freshtrack.freshtrack.batch;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import com.freshtrack.freshtrack.common.enums.BatchStatus;


@Repository
public interface BatchRepository extends JpaRepository<Batch, Long>{
    List<Batch>findByIngredientIdAndStatusInOrderByExpiryDateAsc(
        Long ingredientId,
        List<BatchStatus> statuses
    );
    List<Batch> findByStatus(BatchStatus status);

    @Query("""
            SELECT b FROM Batch b
            WHERE b.ingredient.id = :ingredientId
            AND b.status IN ('ACTIVE', 'EXPIRING_SOON')
            ORDER BY b.expiryDate ASC
            """)
    List <Batch> findAvailableBatchesForIngredient(Long ingredientId);
}
