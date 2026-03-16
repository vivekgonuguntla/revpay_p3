export interface Transaction {
    id: number;
    senderEmail?: string;
    receiverEmail?: string;
    senderName?: string;
    receiverName?: string;
    amount: number;
    type: 'SEND' | 'RECEIVE' | 'REQUEST' | 'DEPOSIT' | 'WITHDRAWAL' | 'PAYMENT';
    status: 'PENDING' | 'COMPLETED' | 'FAILED';
    description: string;
    timestamp: string;
}

export interface TransactionHistoryResponse {
    transactions: Transaction[];
    totalCount: number;
}

export interface SendMoneyRequest {
    receiverEmail: string;
    amount: number;
    pin: string;
    description: string | null;
}
