package com.revpay.business.dto;

import com.revpay.business.entity.BusinessVerificationStatus;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class BusinessProfileResponse {
    private Long id;
    private Long userId;
    private String businessName;
    private String businessType;
    private String taxId;
    private String businessAddress;
    private String verificationDocsPath;
    private BusinessVerificationStatus verificationStatus;
    private LocalDateTime createdAt;
}
