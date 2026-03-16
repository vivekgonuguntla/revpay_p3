export enum CardType {
  VISA = 'VISA',
  MASTERCARD = 'MASTERCARD',
  AMEX = 'AMEX',
  DISCOVER = 'DISCOVER',
  OTHER = 'OTHER'
}

export type PaymentMethodType = 'DEBIT' | 'CREDIT';

export interface PaymentMethod {
  id: number;
  cardHolderName: string;
  cardType: CardType;
  lastFourDigits: string;
  expiryDate: string;
  paymentMethodType: PaymentMethodType;
  isDefault: boolean;
}

export interface AddCardRequest {
  cardHolderName: string;
  cardNumber: string;
  expiryDate: string;
  cvv: string;
  paymentMethodType: PaymentMethodType;
  setAsDefault?: boolean;
}

export interface SetDefaultCardRequest {
  cardId: number;
}
