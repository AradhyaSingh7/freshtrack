package com.freshtrack.freshtrack.ingredient;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/ingredients")
@RequiredArgsConstructor
public class IngredientController {
    private final IngredientService ingredientService;

    @PostMapping
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<IngredientResponse> create(
        @RequestBody @Valid IngredientRequest request){
            return ResponseEntity 
            .status(HttpStatus.CREATED)
            .body(ingredientService.create(request));
        }
    @GetMapping
    @PreAuthorize("hasAnyRole('MANAGER', 'STAFF')")
    public ResponseEntity<List<IngredientResponse>>getAll(){
        return ResponseEntity.ok(ingredientService.getAll());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('MANAGER', 'STAFF')")
    public ResponseEntity<IngredientResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ingredientService.getById(id));
    }
    @GetMapping("/low-stock")
    @PreAuthorize("hasAnyRole('MANAGER', 'STAFF')")
    public ResponseEntity<List<IngredientResponse>> getLowStock() {
        return ResponseEntity.ok(ingredientService.getLowStock());
    }
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<IngredientResponse> update(
            @PathVariable Long id,
            @RequestBody @Valid IngredientRequest request) {
        return ResponseEntity.ok(ingredientService.update(id, request));
    }
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        ingredientService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
