package com.revpay.business.repository;

import com.revpay.business.entity.BusinessLoan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BusinessLoanRepository extends JpaRepository<BusinessLoan, Long> {
    List<BusinessLoan> findByUserIdOrderByAppliedAtDesc(Long userId);
}
