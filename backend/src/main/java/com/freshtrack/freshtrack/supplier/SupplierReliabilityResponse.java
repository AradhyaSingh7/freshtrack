package com.freshtrack.freshtrack.supplier;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class SupplierReliabilityResponse {
    private Long supplierId;
    private String supplierName;
    private Long totalDeliveries;
    private BigDecimal avgShelfLifeDays;
    private Long freshnessRank;
}