package com.revpay.business.controller;

import com.revpay.business.dto.BusinessAnalyticsResponse;
import com.revpay.business.service.AnalyticsService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/business/analytics")
public class AnalyticsController {
    private final AnalyticsService analyticsService;

    public AnalyticsController(AnalyticsService analyticsService) {
        this.analyticsService = analyticsService;
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<BusinessAnalyticsResponse> getAnalytics(@PathVariable Long userId) {
        return ResponseEntity.ok(analyticsService.getAnalytics(userId));
    }
}
