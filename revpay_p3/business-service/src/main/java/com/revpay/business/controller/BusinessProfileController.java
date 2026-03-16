package com.revpay.business.controller;

import com.revpay.business.dto.BusinessProfileResponse;
import com.revpay.business.dto.BusinessVerificationRequest;
import com.revpay.business.entity.BusinessVerificationStatus;
import com.revpay.business.service.BusinessProfileService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/business/profile")
public class BusinessProfileController {
    private final BusinessProfileService businessProfileService;

    public BusinessProfileController(BusinessProfileService businessProfileService) {
        this.businessProfileService = businessProfileService;
    }

    @GetMapping
    public ResponseEntity<BusinessProfileResponse> getProfileByUserId(@RequestHeader("X-User-Id") Long userId){
        return ResponseEntity.ok(businessProfileService.getProfileByUserId(userId));
    }

    @PostMapping("/verification")
    public ResponseEntity<BusinessProfileResponse> createOrUpdateProfile(
            @RequestHeader("X-User-Id") Long userId,
            @Valid @RequestBody BusinessVerificationRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(businessProfileService.createOrUpdateProfile(userId, request));
    }

    @PutMapping("/{profileId}/status")
    public ResponseEntity<BusinessProfileResponse> updateVerificationStatus(
            @PathVariable Long profileId,
            @RequestParam BusinessVerificationStatus status) {
        return ResponseEntity.ok(businessProfileService.updateVerificationStatus(profileId, status));
    }
}
