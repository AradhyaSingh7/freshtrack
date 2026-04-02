package com.freshtrack.freshtrack.dish;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/dishes")
@RequiredArgsConstructor
public class DishController {
    private final DishService dishService;
    @PostMapping
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<DishResponse> create(@RequestBody @Valid DishRequest request){
        return ResponseEntity.status(HttpStatus.CREATED).body(dishService.create(request));
    }
    @GetMapping
    @PreAuthorize("hasAnyRole('MANAGER', 'STAFF')")
    public ResponseEntity<List<DishResponse>> getAll(){
        return ResponseEntity.ok(dishService.getAll());
    }
    @GetMapping("/active")
    @PreAuthorize("hasAnyRole('MANAGER', 'STAFF')")
    public ResponseEntity<List<DishResponse>> getActive(){
        return ResponseEntity.ok(dishService.getActive());
    }
    @GetMapping("/suggested")
    @PreAuthorize("hasAnyRole('MANAGER', 'STAFF')")
    public ResponseEntity<List<DishResponse>> getSugessted(){
        return ResponseEntity.ok(dishService.getSuggested());
    }
}
