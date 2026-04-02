package com.freshtrack.freshtrack.supplier;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class SupplierRequest {
    @NotBlank(message = "Supplier name is required")
    private String name;
    
    private String contactPerson;
    private String phone;
    @Email(message = "Must be a valid email")
    private String email;
}
