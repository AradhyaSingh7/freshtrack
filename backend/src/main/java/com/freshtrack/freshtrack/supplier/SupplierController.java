package com.freshtrack.freshtrack.supplier;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;



@RestController
@RequestMapping("/api/suppliers")
@RequiredArgsConstructor
public class SupplierController {
    private final SupplierService supplierService;

    @PostMapping
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity <SupplierResponse> create (
        @RequestBody @Valid SupplierRequest request){
            return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(supplierService.create(request));
        }
    @GetMapping
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<List<SupplierResponse>> getAll() {
        return ResponseEntity.ok(supplierService.getAll());
    }
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<SupplierResponse>getById(@PathVariable Long id){
        return ResponseEntity.ok(supplierService.getById(id));
    }
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<SupplierResponse> update(@PathVariable Long id, @RequestBody @Valid SupplierRequest request){
        return ResponseEntity.ok(supplierService.update(id, request));
    }
    @GetMapping("/reliability")
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<List<SupplierReliabilityResponse>> getReliability() {
        return ResponseEntity.ok(supplierService.getReliabilityRankings());
    }
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<Void>delete(@PathVariable Long id){
        supplierService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
