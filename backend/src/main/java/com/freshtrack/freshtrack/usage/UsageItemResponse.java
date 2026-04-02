package com.freshtrack.freshtrack.usage;

import java.math.BigDecimal;
import java.time.LocalDate;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UsageItemResponse {
    private Long batchId;
    private String ingredientName;
    private BigDecimal quantityDeducted;
    private LocalDate batchExpiryDate;
}
