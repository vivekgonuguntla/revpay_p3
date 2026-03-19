package com.revpay.business.repository;

import com.revpay.business.entity.Invoice;
import com.revpay.business.entity.InvoiceStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, Long> {
    List<Invoice> findByInvoiceNumberContainingIgnoreCase(String invoiceNumber);
    List<Invoice> findByCustomerPhoneContainingIgnoreCase(String customerPhone);
    List<Invoice> findByCustomerEmailContainingIgnoreCase(String customerEmail);
    List<Invoice> findByCustomerIdContainingIgnoreCase(String customerId);
    List<Invoice> findByUserIdOrderByCreatedAtDesc(Long userId);
    List<Invoice> findByUserIdAndStatusOrderByCreatedAtDesc(Long userId, InvoiceStatus status);
    List<Invoice> findByStatusAndDueDateBefore(InvoiceStatus status, LocalDate date);
}
