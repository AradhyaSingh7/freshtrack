package com.freshtrack.freshtrack.waste;

import java.math.BigDecimal;

import com.freshtrack.freshtrack.common.enums.WasteReason;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class WasteAnalyticsResponse {
    private WasteReason reason;
    private Long count;
    private BigDecimal totalCost;
}
