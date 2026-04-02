package com.freshtrack.freshtrack.batch;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DeductionItem {
    private Batch batch;
    private BigDecimal quantityDeducted;
}
