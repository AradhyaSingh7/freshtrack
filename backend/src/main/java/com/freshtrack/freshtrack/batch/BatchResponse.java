package com.freshtrack.freshtrack.batch;

import com.freshtrack.freshtrack.common.enums.BatchStatus;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
public class BatchResponse {
    private Long id;
    private Long ingredientId;
    private String ingredientName;
    private Long supplierId;
    private String supplierName;
    private BigDecimal quantityOriginal;
    private BigDecimal quantityRemaining;
    private BigDecimal costPerUnit;
    private LocalDate expiryDate;
    private LocalDate receivedDate;
    private BatchStatus status;
    private LocalDateTime createdAt;
}