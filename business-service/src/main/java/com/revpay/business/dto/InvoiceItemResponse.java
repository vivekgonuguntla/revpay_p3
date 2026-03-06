package com.revpay.business.dto;

import java.math.BigDecimal;

public class InvoiceItemResponse {
    private Long id;
    private String itemName;
    private Integer quantity;
    private BigDecimal unitPrice;
    private BigDecimal total;

    public InvoiceItemResponse() {
    }

    public InvoiceItemResponse(Long id, String itemName, Integer quantity, BigDecimal unitPrice, BigDecimal total) {
        this.id = id;
        this.itemName = itemName;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.total = total;
    }

    public static InvoiceItemResponseBuilder builder() {
        return new InvoiceItemResponseBuilder();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    public static class InvoiceItemResponseBuilder {
        private Long id;
        private String itemName;
        private Integer quantity;
        private BigDecimal unitPrice;
        private BigDecimal total;

        public InvoiceItemResponseBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public InvoiceItemResponseBuilder itemName(String itemName) {
            this.itemName = itemName;
            return this;
        }

        public InvoiceItemResponseBuilder quantity(Integer quantity) {
            this.quantity = quantity;
            return this;
        }

        public InvoiceItemResponseBuilder unitPrice(BigDecimal unitPrice) {
            this.unitPrice = unitPrice;
            return this;
        }

        public InvoiceItemResponseBuilder total(BigDecimal total) {
            this.total = total;
            return this;
        }

        public InvoiceItemResponse build() {
            return new InvoiceItemResponse(id, itemName, quantity, unitPrice, total);
        }
    }
}
