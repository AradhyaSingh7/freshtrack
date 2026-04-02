package com.freshtrack.freshtrack.supplier;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface SupplierRepository extends JpaRepository<Supplier, Long> {
    @Query(value = """
    SELECT s.id, s.name,
           COUNT(b.id) AS total_deliveries,
           ROUND(AVG(DATEDIFF(b.expiry_date, b.received_date)), 1) AS avg_shelf_life_days,
           RANK() OVER (
               ORDER BY AVG(DATEDIFF(b.expiry_date, b.received_date)) DESC
           ) AS freshness_rank
    FROM suppliers s
    JOIN batches b ON b.supplier_id = s.id
    GROUP BY s.id, s.name
    """, nativeQuery = true)
List<Object[]> getSupplierReliability();
}
