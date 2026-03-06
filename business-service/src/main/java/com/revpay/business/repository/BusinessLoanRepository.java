package com.revpay.business.repository;

import com.revpay.business.entity.BusinessLoan;
import com.revpay.business.entity.LoanStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BusinessLoanRepository extends JpaRepository<BusinessLoan, Long> {
    List<BusinessLoan> findByUserIdOrderByAppliedAtDesc(Long userId);
    Long countByUserId(Long userId);
    Long countByUserIdAndStatus(Long userId, LoanStatus status);
}
