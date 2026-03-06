package com.revpay.business.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "business_profiles")
public class BusinessProfile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private Long userId;

    @Column(nullable = false)
    private String businessName;

    @Column(nullable = false)
    private String businessType;

    @Column(unique = true)
    private String taxId;

    @Column(length = 500)
    private String businessAddress;

    @Column(length = 1000)
    private String verificationDocsPath;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BusinessVerificationStatus verificationStatus;

    private LocalDateTime createdAt;

    public BusinessProfile() {
    }

    public BusinessProfile(Long id, Long userId, String businessName, String businessType, String taxId, String businessAddress, String verificationDocsPath, BusinessVerificationStatus verificationStatus, LocalDateTime createdAt) {
        this.id = id;
        this.userId = userId;
        this.businessName = businessName;
        this.businessType = businessType;
        this.taxId = taxId;
        this.businessAddress = businessAddress;
        this.verificationDocsPath = verificationDocsPath;
        this.verificationStatus = verificationStatus;
        this.createdAt = createdAt;
    }

    public static BusinessProfileBuilder builder() {
        return new BusinessProfileBuilder();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getBusinessName() {
        return businessName;
    }

    public void setBusinessName(String businessName) {
        this.businessName = businessName;
    }

    public String getBusinessType() {
        return businessType;
    }

    public void setBusinessType(String businessType) {
        this.businessType = businessType;
    }

    public String getTaxId() {
        return taxId;
    }

    public void setTaxId(String taxId) {
        this.taxId = taxId;
    }

    public String getBusinessAddress() {
        return businessAddress;
    }

    public void setBusinessAddress(String businessAddress) {
        this.businessAddress = businessAddress;
    }

    public String getVerificationDocsPath() {
        return verificationDocsPath;
    }

    public void setVerificationDocsPath(String verificationDocsPath) {
        this.verificationDocsPath = verificationDocsPath;
    }

    public BusinessVerificationStatus getVerificationStatus() {
        return verificationStatus;
    }

    public void setVerificationStatus(BusinessVerificationStatus verificationStatus) {
        this.verificationStatus = verificationStatus;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    @PrePersist
    public void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
        if (verificationStatus == null) {
            verificationStatus = BusinessVerificationStatus.NOT_SUBMITTED;
        }
    }

    public static class BusinessProfileBuilder {
        private Long id;
        private Long userId;
        private String businessName;
        private String businessType;
        private String taxId;
        private String businessAddress;
        private String verificationDocsPath;
        private BusinessVerificationStatus verificationStatus;
        private LocalDateTime createdAt;

        public BusinessProfileBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public BusinessProfileBuilder userId(Long userId) {
            this.userId = userId;
            return this;
        }

        public BusinessProfileBuilder businessName(String businessName) {
            this.businessName = businessName;
            return this;
        }

        public BusinessProfileBuilder businessType(String businessType) {
            this.businessType = businessType;
            return this;
        }

        public BusinessProfileBuilder taxId(String taxId) {
            this.taxId = taxId;
            return this;
        }

        public BusinessProfileBuilder businessAddress(String businessAddress) {
            this.businessAddress = businessAddress;
            return this;
        }

        public BusinessProfileBuilder verificationDocsPath(String verificationDocsPath) {
            this.verificationDocsPath = verificationDocsPath;
            return this;
        }

        public BusinessProfileBuilder verificationStatus(BusinessVerificationStatus verificationStatus) {
            this.verificationStatus = verificationStatus;
            return this;
        }

        public BusinessProfileBuilder createdAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public BusinessProfile build() {
            return new BusinessProfile(id, userId, businessName, businessType, taxId, businessAddress, verificationDocsPath, verificationStatus, createdAt);
        }
    }
}
