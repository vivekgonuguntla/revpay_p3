package com.revpay.business.repository;

import com.revpay.business.entity.Invoice;
import com.revpay.business.entity.InvoiceStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, Long> {
    Optional<Invoice> findByInvoiceNumber(String invoiceNumber);
    List<Invoice> findByUserIdOrderByCreatedAtDesc(Long userId);
    List<Invoice> findByUserIdAndStatusOrderByCreatedAtDesc(Long userId, InvoiceStatus status);
    List<Invoice> findByStatusAndDueDateBefore(InvoiceStatus status, LocalDate date);
    Long countByUserId(Long userId);
    Long countByUserIdAndStatus(Long userId, InvoiceStatus status);
}
