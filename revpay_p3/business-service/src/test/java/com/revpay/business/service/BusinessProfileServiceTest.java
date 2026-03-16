package com.revpay.business.service;

import com.revpay.business.client.NotificationServiceClient;
import com.revpay.business.dto.BusinessProfileResponse;
import com.revpay.business.dto.BusinessVerificationRequest;
import com.revpay.business.entity.BusinessProfile;
import com.revpay.business.entity.BusinessVerificationStatus;
import com.revpay.business.repository.BusinessProfileRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BusinessProfileServiceTest {

    @Mock
    private BusinessProfileRepository businessProfileRepository;

    @Mock
    private NotificationServiceClient notificationServiceClient;

    @InjectMocks
    private BusinessProfileService businessProfileService;

    @Test
    void getProfileByUserIdReturnsDefaultProfileWhenMissing() {
        when(businessProfileRepository.findByUserId(9L)).thenReturn(Optional.empty());

        BusinessProfileResponse response = businessProfileService.getProfileByUserId(9L);

        assertEquals(9L, response.getUserId());
        assertEquals(BusinessVerificationStatus.NOT_SUBMITTED, response.getVerificationStatus());
    }

    @Test
    void createOrUpdateProfileSavesProfileAndSendsNotification() {
        BusinessVerificationRequest request = new BusinessVerificationRequest();
        request.setBusinessName("Acme");
        request.setBusinessType("Retail");
        request.setTaxId("TAX-99");
        request.setBusinessAddress("Bangalore");
        request.setVerificationDocsPath("/docs/verify.pdf");

        when(businessProfileRepository.findByUserId(9L)).thenReturn(Optional.empty());
        when(businessProfileRepository.save(any(BusinessProfile.class))).thenAnswer(invocation -> {
            BusinessProfile profile = invocation.getArgument(0);
            profile.setId(11L);
            return profile;
        });

        BusinessProfileResponse response = businessProfileService.createOrUpdateProfile(9L, request);

        ArgumentCaptor<BusinessProfile> profileCaptor = ArgumentCaptor.forClass(BusinessProfile.class);
        verify(businessProfileRepository).save(profileCaptor.capture());
        BusinessProfile saved = profileCaptor.getValue();
        assertEquals("Acme", saved.getBusinessName());
        assertEquals(BusinessVerificationStatus.PENDING_VERIFICATION, saved.getVerificationStatus());
        assertEquals(11L, response.getId());
        verify(notificationServiceClient).sendNotification(any(NotificationServiceClient.NotificationRequest.class));
    }

    @Test
    void updateVerificationStatusThrowsWhenProfileMissing() {
        when(businessProfileRepository.findById(4L)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> businessProfileService.updateVerificationStatus(4L, BusinessVerificationStatus.VERIFIED));

        assertEquals("Business profile not found: 4", exception.getMessage());
    }

    @Test
    void updateVerificationStatusStillReturnsSavedProfileWhenNotificationFails() {
        BusinessProfile profile = BusinessProfile.builder()
                .id(4L)
                .userId(9L)
                .businessName("Acme")
                .businessType("Retail")
                .verificationStatus(BusinessVerificationStatus.PENDING_VERIFICATION)
                .build();

        when(businessProfileRepository.findById(4L)).thenReturn(Optional.of(profile));
        when(businessProfileRepository.save(profile)).thenReturn(profile);
        doThrow(new RuntimeException("notify failed"))
                .when(notificationServiceClient).sendNotification(any(NotificationServiceClient.NotificationRequest.class));

        BusinessProfileResponse response =
                businessProfileService.updateVerificationStatus(4L, BusinessVerificationStatus.VERIFIED);

        assertEquals(BusinessVerificationStatus.VERIFIED, response.getVerificationStatus());
        assertEquals(BusinessVerificationStatus.VERIFIED, profile.getVerificationStatus());
    }
}
