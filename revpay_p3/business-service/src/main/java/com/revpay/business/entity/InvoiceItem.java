package com.revpay.business.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Table(name = "invoice_items")
public class InvoiceItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "invoice_id", nullable = false)
    private Invoice invoice;

    @Column(nullable = false)
    private String itemName;

    @Column(nullable = false)
    private Integer quantity;

    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal unitPrice;

    public InvoiceItem() {
    }

    public InvoiceItem(Long id, Invoice invoice, String itemName, Integer quantity, BigDecimal unitPrice) {
        this.id = id;
        this.invoice = invoice;
        this.itemName = itemName;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
    }

    public static InvoiceItemBuilder builder() {
        return new InvoiceItemBuilder();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Invoice getInvoice() {
        return invoice;
    }

    public void setInvoice(Invoice invoice) {
        this.invoice = invoice;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
    }

    public static class InvoiceItemBuilder {
        private Long id;
        private Invoice invoice;
        private String itemName;
        private Integer quantity;
        private BigDecimal unitPrice;

        public InvoiceItemBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public InvoiceItemBuilder invoice(Invoice invoice) {
            this.invoice = invoice;
            return this;
        }

        public InvoiceItemBuilder itemName(String itemName) {
            this.itemName = itemName;
            return this;
        }

        public InvoiceItemBuilder quantity(Integer quantity) {
            this.quantity = quantity;
            return this;
        }

        public InvoiceItemBuilder unitPrice(BigDecimal unitPrice) {
            this.unitPrice = unitPrice;
            return this;
        }

        public InvoiceItem build() {
            return new InvoiceItem(id, invoice, itemName, quantity, unitPrice);
        }
    }
}
