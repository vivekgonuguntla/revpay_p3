package com.revpay.business.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "invoices")
public class Invoice {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false, unique = true)
    private String invoiceNumber;

    @Column(nullable = false)
    private String customerName;

    private String customerEmail;

    private String customerPhone;

    private String customerId;

    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal amount;

    @Column(nullable = false)
    private String currency;

    @Column(nullable = false)
    private LocalDate dueDate;

    @Column(length = 1000)
    private String description;

    @Column(length = 1000)
    private String paymentTerms;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private InvoiceStatus status;

    private LocalDateTime createdAt;

    private LocalDateTime paidAt;

    @OneToMany(mappedBy = "invoice", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<InvoiceItem> items;

    public Invoice() {
    }

    public Invoice(Long id, Long userId, String invoiceNumber, String customerName, String customerEmail, String customerPhone, String customerId, BigDecimal amount, String currency, LocalDate dueDate, String description, String paymentTerms, InvoiceStatus status, LocalDateTime createdAt, LocalDateTime paidAt, List<InvoiceItem> items) {
        this.id = id;
        this.userId = userId;
        this.invoiceNumber = invoiceNumber;
        this.customerName = customerName;
        this.customerEmail = customerEmail;
        this.customerPhone = customerPhone;
        this.customerId = customerId;
        this.amount = amount;
        this.currency = currency;
        this.dueDate = dueDate;
        this.description = description;
        this.paymentTerms = paymentTerms;
        this.status = status;
        this.createdAt = createdAt;
        this.paidAt = paidAt;
        this.items = items;
    }

    public static InvoiceBuilder builder() {
        return new InvoiceBuilder();
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

    public String getInvoiceNumber() {
        return invoiceNumber;
    }

    public void setInvoiceNumber(String invoiceNumber) {
        this.invoiceNumber = invoiceNumber;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getCustomerEmail() {
        return customerEmail;
    }

    public void setCustomerEmail(String customerEmail) {
        this.customerEmail = customerEmail;
    }

    public String getCustomerPhone() {
        return customerPhone;
    }

    public void setCustomerPhone(String customerPhone) {
        this.customerPhone = customerPhone;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPaymentTerms() {
        return paymentTerms;
    }

    public void setPaymentTerms(String paymentTerms) {
        this.paymentTerms = paymentTerms;
    }

    public InvoiceStatus getStatus() {
        return status;
    }

    public void setStatus(InvoiceStatus status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getPaidAt() {
        return paidAt;
    }

    public void setPaidAt(LocalDateTime paidAt) {
        this.paidAt = paidAt;
    }

    public List<InvoiceItem> getItems() {
        return items;
    }

    public void setItems(List<InvoiceItem> items) {
        this.items = items;
    }

    @PrePersist
    public void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
        if (status == null) {
            status = InvoiceStatus.SENT;
        }
    }

    public static class InvoiceBuilder {
        private Long id;
        private Long userId;
        private String invoiceNumber;
        private String customerName;
        private String customerEmail;
        private String customerPhone;
        private String customerId;
        private BigDecimal amount;
        private String currency;
        private LocalDate dueDate;
        private String description;
        private String paymentTerms;
        private InvoiceStatus status;
        private LocalDateTime createdAt;
        private LocalDateTime paidAt;
        private List<InvoiceItem> items;

        public InvoiceBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public InvoiceBuilder userId(Long userId) {
            this.userId = userId;
            return this;
        }

        public InvoiceBuilder invoiceNumber(String invoiceNumber) {
            this.invoiceNumber = invoiceNumber;
            return this;
        }

        public InvoiceBuilder customerName(String customerName) {
            this.customerName = customerName;
            return this;
        }

        public InvoiceBuilder customerEmail(String customerEmail) {
            this.customerEmail = customerEmail;
            return this;
        }

        public InvoiceBuilder customerPhone(String customerPhone) {
            this.customerPhone = customerPhone;
            return this;
        }

        public InvoiceBuilder customerId(String customerId) {
            this.customerId = customerId;
            return this;
        }

        public InvoiceBuilder amount(BigDecimal amount) {
            this.amount = amount;
            return this;
        }

        public InvoiceBuilder currency(String currency) {
            this.currency = currency;
            return this;
        }

        public InvoiceBuilder dueDate(LocalDate dueDate) {
            this.dueDate = dueDate;
            return this;
        }

        public InvoiceBuilder description(String description) {
            this.description = description;
            return this;
        }

        public InvoiceBuilder paymentTerms(String paymentTerms) {
            this.paymentTerms = paymentTerms;
            return this;
        }

        public InvoiceBuilder status(InvoiceStatus status) {
            this.status = status;
            return this;
        }

        public InvoiceBuilder createdAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public InvoiceBuilder paidAt(LocalDateTime paidAt) {
            this.paidAt = paidAt;
            return this;
        }

        public InvoiceBuilder items(List<InvoiceItem> items) {
            this.items = items;
            return this;
        }

        public Invoice build() {
            return new Invoice(id, userId, invoiceNumber, customerName, customerEmail, customerPhone, customerId, amount, currency, dueDate, description, paymentTerms, status, createdAt, paidAt, items);
        }
    }
}
