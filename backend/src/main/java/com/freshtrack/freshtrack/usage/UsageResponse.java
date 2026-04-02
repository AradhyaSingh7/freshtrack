package com.freshtrack.freshtrack.usage;

import java.time.LocalDateTime;
import java.util.List;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UsageResponse {
    private Long id;
    private Long dishId;
    private String dishName;
    private Integer portionsPrepared;
    private String loggedBy;
    private LocalDateTime loggedAt;
    private List<UsageItemResponse> deductions;
}
