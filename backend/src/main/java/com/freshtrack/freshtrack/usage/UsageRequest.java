package com.freshtrack.freshtrack.usage;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UsageRequest {
    @NotNull(message="Dish id is required")
    private Long dishId;
    
    @NotNull(message = "Portions are required")
    @Min(value=1, message="Must prepare at least 1 portion")
    private Integer portions;
}
