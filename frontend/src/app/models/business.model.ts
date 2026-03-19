export type VerificationStatus = 'NOT_SUBMITTED' | 'PENDING_VERIFICATION' | 'VERIFIED' | 'REJECTED';
export type InvoiceStatus = 'SENT' | 'PAID' | 'OVERDUE';
export type LoanStatus = 'SUBMITTED' | 'APPROVED' | 'REJECTED' | 'ADDITIONAL_DOCUMENTS_REQUIRED';
export type RepaymentStatus = 'PENDING' | 'PAID' | 'OVERDUE';

export interface InvoiceItemRequest {
    itemName: string;
    quantity: number;
    unitPrice: number;
}

export interface CreateInvoiceRequest {
    customerName: string;
    customerEmail?: string;
    customerPhone?: string;
    customerId?: string;
    currency: string;
    dueDate: string;
    description?: string;
    paymentTerms?: string;
    items: InvoiceItemRequest[];
}

export interface InvoiceItemResponse {
    id: number;
    itemName: string;
    quantity: number;
    unitPrice: number;
    lineTotal: number;
}

export interface InvoiceResponse {
    id: number;
    invoiceNumber: string;
    customerName: string;
    customerEmail?: string;
    customerPhone?: string;
    customerId?: string;
    amount: number;
    currency: string;
    dueDate: string;
    description?: string;
    paymentTerms?: string;
    status: InvoiceStatus;
    createdAt: string;
    paidAt?: string;
    items: InvoiceItemResponse[];
}

export interface InvoicePaymentRequest {
    lookupType: 'INVOICE_NUMBER' | 'PHONE' | 'EMAIL' | 'CUSTOMER_ID';
    lookupValue: string;
}

export interface LoanApplicationRequest {
    loanAmount: number;
    purpose: string;
    financialDetails: string;
    supportingDocumentsPath?: string;
    termMonths: number;
}

export interface LoanRepaymentResponse {
    id: number;
    installmentNumber: number;
    amount: number;
    dueDate: string;
    status: RepaymentStatus;
    paidAt?: string;
}

export interface LoanResponse {
    id: number;
    loanAmount: number;
    purpose: string;
    financialDetails: string;
    supportingDocumentsPath?: string;
    status: LoanStatus;
    termMonths: number;
    remainingBalance: number;
    appliedAt: string;
    repayments: LoanRepaymentResponse[];
}

export interface BusinessAnalyticsResponse {
    totalRevenue: number;
    transactionSummary: {
        totalTransactions: number;
        totalReceived: number;
        totalSent: number;
    };
    outstandingInvoices: {
        count: number;
        totalAmount: number;
    };
    paymentTrends: {
        date: string;
        amount: number;
    }[];
    topCustomers: {
        customer: string;
        totalPaid: number;
    }[];
}

