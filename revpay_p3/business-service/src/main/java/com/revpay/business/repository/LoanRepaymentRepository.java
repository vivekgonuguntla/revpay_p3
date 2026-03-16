package com.revpay.business.repository;

import com.revpay.business.entity.BusinessLoan;
import com.revpay.business.entity.LoanRepayment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LoanRepaymentRepository extends JpaRepository<LoanRepayment, Long> {
    List<LoanRepayment> findByLoanOrderByDueDateAsc(BusinessLoan loan);

    @Query("SELECT lr FROM LoanRepayment lr WHERE lr.id = :id AND lr.loan.id = :loanId")
    Optional<LoanRepayment> findByIdAndLoanId(@Param("id") Long id, @Param("loanId") Long loanId);
}
