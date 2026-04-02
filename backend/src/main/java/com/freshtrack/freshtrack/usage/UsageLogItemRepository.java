package com.freshtrack.freshtrack.usage;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UsageLogItemRepository extends JpaRepository<UsageLogItem, Long>{
}
