import { CardType, PaymentMethodType } from './payment-method.model';

export interface Card {
    id: number;
    cardHolderName: string;
    lastFourDigits: string;
    expiryDate: string;
    cardType: CardType;
    paymentMethodType: PaymentMethodType;
    isDefault: boolean;
}

export interface AddCardRequest {
    cardHolderName: string;
    cardNumber: string;
    expiryDate: string;
    cvv: string;
    paymentMethodType: PaymentMethodType;
    setAsDefault: boolean;
}
