package com.freshtrack.freshtrack.usage;

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
@RequestMapping("/api/usage")
@RequiredArgsConstructor
public class UsageController {
    private final UsageService usageService;

    @PostMapping
    @PreAuthorize("hasAnyRole('MANAGER', 'STAFF')")
    public ResponseEntity<UsageResponse> logUsage(@RequestBody @Valid UsageRequest request){
        return ResponseEntity
        .status(HttpStatus.CREATED)
        .body(usageService.logUsage(request));
    }
    @PostMapping("/preview")
    @PreAuthorize("hasAnyRole('MANAGER', 'STAFF')")
    public ResponseEntity<UsageResponse> previewUsage(
        @RequestBody @Valid UsageRequest request) {
    return ResponseEntity.ok(usageService.previewUsage(request));
}
    @GetMapping("/history")
    @PreAuthorize("hasAnyRole('MANAGER', 'STAFF')")
    public ResponseEntity<List<UsageResponse>> getHistory(){
        return ResponseEntity.ok(usageService.getHistory());
    }
}
