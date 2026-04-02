package com.freshtrack.freshtrack.usage;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UsageLogRepository extends JpaRepository<UsageLog, Long>{
    List<UsageLog>findAllByOrderByLoggedAtDesc();
}
