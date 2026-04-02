package com.freshtrack.freshtrack.waste;

import java.math.BigDecimal;

import com.freshtrack.freshtrack.common.enums.WasteReason;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class WasteRequest {
    @NotNull(message = "Ingredient id is required")
    private Long ingredientId;

    @NotNull(message = "Quantity is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Quantity must be greater than 0")
    private BigDecimal quantity;

    @NotNull(message = "Reason is required")
    private WasteReason reason;

    @NotNull(message = "Please indicate if this was already deducted from inventory")
    private Boolean alreadyDeducted;

    private String notes;
}
