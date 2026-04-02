package com.freshtrack.freshtrack.waste;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import com.freshtrack.freshtrack.common.enums.WasteReason;


@Repository
public interface WasteLogRepository extends JpaRepository<WasteLog, Long> {
    List<WasteLog> findAllByOrderByLoggedAtDesc();
    List<WasteLog> findByReason(WasteReason reason);

    @Query("""
        SELECT w.reason, COUNT(w), SUM(w.costAtTimeOfWaste)
        FROM WasteLog w
        Where w.loggedAt BETWEEN :start AND :end
        GROUP BY w.reason
            """)
        List<Object[]>getWasteAnalyticsByReason(LocalDateTime start, LocalDateTime end);
}
