package com.freshtrack.freshtrack.waste;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.freshtrack.freshtrack.common.enums.WasteReason;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class WasteResponse {
    private Long id;
    private Long ingredientId;
    private String ingredientName;
    private BigDecimal quantityWasted;
    private WasteReason reason;
    private Boolean alreadyDeducted;
    private String notes;
    private BigDecimal costAtTimeOfWaste;
    private String loggedBy;
    private LocalDateTime loggedAt;
}
