package com.revpay.business.service;

import com.revpay.business.client.NotificationServiceClient;
import com.revpay.business.dto.BusinessProfileResponse;
import com.revpay.business.dto.BusinessVerificationRequest;
import com.revpay.business.entity.BusinessProfile;
import com.revpay.business.entity.BusinessVerificationStatus;
import com.revpay.business.repository.BusinessProfileRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class BusinessProfileService {
    private final BusinessProfileRepository businessProfileRepository;
    private final NotificationServiceClient notificationServiceClient;

    public BusinessProfileService(BusinessProfileRepository businessProfileRepository, NotificationServiceClient notificationServiceClient) {
        this.businessProfileRepository = businessProfileRepository;
        this.notificationServiceClient = notificationServiceClient;
    }

    public BusinessProfileResponse getProfileByUserId(Long userId) {
        BusinessProfile profile = businessProfileRepository.findByUserId(userId)
                .orElse(BusinessProfile.builder()
                        .userId(userId)
                        .verificationStatus(BusinessVerificationStatus.NOT_SUBMITTED)
                        .build());
        return mapToResponse(profile);
    }

    @Transactional
    public BusinessProfileResponse createOrUpdateProfile(Long userId, BusinessVerificationRequest request) {
        BusinessProfile profile = businessProfileRepository.findByUserId(userId)
                .orElse(BusinessProfile.builder()
                        .userId(userId)
                        .build());

        profile.setBusinessName(request.getBusinessName());
        profile.setBusinessType(request.getBusinessType());
        profile.setTaxId(request.getTaxId());
        profile.setBusinessAddress(request.getBusinessAddress());
        profile.setVerificationDocsPath(request.getVerificationDocsPath());
        profile.setVerificationStatus(BusinessVerificationStatus.PENDING_VERIFICATION);

        BusinessProfile saved = businessProfileRepository.save(profile);

        // Send notification
        try {
            NotificationServiceClient.NotificationRequest notification = new NotificationServiceClient.NotificationRequest();
            notification.userId = userId;
            notification.category = "ALERTS";
            notification.title = "Business Verification Submitted";
            notification.message = "Your business verification documents have been submitted for review.";
            notification.type = "BUSINESS_VERIFICATION";
            notification.eventStatus = "PENDING_VERIFICATION";
            notification.navigationTarget = "/business";
            notification.eventTime = java.time.LocalDateTime.now();
            notificationServiceClient.sendNotification(notification);
        } catch (Exception e) {
            // Log error but don't fail the transaction
        }

        return mapToResponse(saved);
    }

    @Transactional
    public BusinessProfileResponse updateVerificationStatus(Long profileId, BusinessVerificationStatus status) {
        BusinessProfile profile = businessProfileRepository.findById(profileId)
                .orElseThrow(() -> new RuntimeException("Business profile not found: " + profileId));

        profile.setVerificationStatus(status);
        BusinessProfile saved = businessProfileRepository.save(profile);

        // Send notification
        try {
            NotificationServiceClient.NotificationRequest notification = new NotificationServiceClient.NotificationRequest();
            notification.userId = profile.getUserId();
            notification.category = "ALERTS";
            notification.title = "Business Verification " + status.name();
            notification.message = "Your business verification status has been updated to: " + status.name();
            notification.type = "BUSINESS_VERIFICATION_UPDATE";
            notification.eventStatus = status.name();
            notification.navigationTarget = "/business";
            notification.eventTime = java.time.LocalDateTime.now();
            notificationServiceClient.sendNotification(notification);
        } catch (Exception e) {
            // Log error but don't fail the transaction
        }

        return mapToResponse(saved);
    }

    private BusinessProfileResponse mapToResponse(BusinessProfile profile) {
        return BusinessProfileResponse.builder()
                .id(profile.getId())
                .userId(profile.getUserId())
                .businessName(profile.getBusinessName())
                .businessType(profile.getBusinessType())
                .taxId(profile.getTaxId())
                .businessAddress(profile.getBusinessAddress())
                .verificationDocsPath(profile.getVerificationDocsPath())
                .verificationStatus(profile.getVerificationStatus())
                .createdAt(profile.getCreatedAt())
                .build();
    }
}
