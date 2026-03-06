package com.revpay.business.repository;

import com.revpay.business.entity.BusinessLoan;
import com.revpay.business.entity.LoanRepayment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LoanRepaymentRepository extends JpaRepository<LoanRepayment, Long> {
    List<LoanRepayment> findByLoanOrderByDueDateAsc(BusinessLoan loan);
}
