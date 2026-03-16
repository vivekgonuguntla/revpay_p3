package com.revpay.business.controller;

import com.revpay.business.dto.BusinessAnalyticsResponse;
import com.revpay.business.service.AnalyticsService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/v1/business/analytics")
public class AnalyticsController {
    private final AnalyticsService analyticsService;

    public AnalyticsController(AnalyticsService analyticsService) {
        this.analyticsService = analyticsService;
    }

    @GetMapping
    public ResponseEntity<BusinessAnalyticsResponse> getAnalytics(
            @RequestHeader("X-User-Id") Long userId,
            @RequestParam(required = false) LocalDate from,
            @RequestParam(required = false) LocalDate to) {
        return ResponseEntity.ok(analyticsService.getAnalytics(userId, from, to));
    }
}
