package com.freshtrack.freshtrack.batch;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/batches")
@RequiredArgsConstructor
public class BatchController {
    private final BatchService batchService;

    @PostMapping
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<BatchResponse> create(@RequestBody @Valid BatchRequest request){
        return ResponseEntity.status(HttpStatus.CREATED).body(batchService.create(request));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('MANAGER', 'STAFF')")
    public ResponseEntity<List<BatchResponse>> getAll(){
        return ResponseEntity.ok(batchService.getAll());
    }

    @GetMapping("/ingredient/{ingredientId}")
    @PreAuthorize("hasAnyRole('MANAGER', 'STAFF')")
    public ResponseEntity<List<BatchResponse>> getByIngredient(
            @PathVariable Long ingredientId) {
        return ResponseEntity.ok(batchService.getByIngredient(ingredientId));
    }
    @GetMapping("/expiring-soon")
    @PreAuthorize("hasAnyRole('MANAGER', 'STAFF')")
    public ResponseEntity<List<BatchResponse>> getExpiringSoon() {
        return ResponseEntity.ok(batchService.getExpiringSoon());
    }
}
