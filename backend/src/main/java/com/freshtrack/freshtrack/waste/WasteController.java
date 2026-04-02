package com.freshtrack.freshtrack.waste;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/waste")
@RequiredArgsConstructor
public class WasteController {
    private final WasteService wasteService;

    @PostMapping
    @PreAuthorize("hasAnyRole('MANAGER', 'STAFF')")
    public ResponseEntity<WasteResponse> logWaste(@RequestBody @Valid WasteRequest request){
        return ResponseEntity.status(HttpStatus.CREATED).body(wasteService.logWaste(request));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('MANAGER', 'STAFF')")
    public ResponseEntity<List<WasteResponse>> getAll() {
        return ResponseEntity.ok(wasteService.getAll());
    }

    @GetMapping("/analytics")
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<List<WasteAnalyticsResponse>> getAnalytics(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime end){
            return ResponseEntity.ok(wasteService.getAnalytics(start, end));
            }
}
