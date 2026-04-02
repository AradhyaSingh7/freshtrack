package com.freshtrack.freshtrack.batch;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@EnableScheduling
@RequiredArgsConstructor
@Slf4j
public class ExpiryScheduler {

    private final BatchService batchService;

    @Scheduled(cron = "0 0 0 * * *")
    public void runNightlyExpiryCheck() {
        log.info("Running nightly expiry check...");
        batchService.updateExpiredBatches();
        log.info("Nightly expiry check complete.");
    }
}