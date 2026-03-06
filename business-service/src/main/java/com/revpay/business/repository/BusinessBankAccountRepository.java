package com.revpay.business.repository;

import com.revpay.business.entity.BusinessBankAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BusinessBankAccountRepository extends JpaRepository<BusinessBankAccount, Long> {
    List<BusinessBankAccount> findByUserId(Long userId);
    boolean existsByUserIdAndIsDefaultTrue(Long userId);

    @Modifying
    @Query("UPDATE BusinessBankAccount b SET b.isDefault = false WHERE b.userId = :userId")
    void clearDefaultForUser(Long userId);
}
