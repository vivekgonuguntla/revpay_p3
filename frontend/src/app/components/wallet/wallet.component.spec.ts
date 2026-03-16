import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ReactiveFormsModule } from '@angular/forms';
import { NO_ERRORS_SCHEMA } from '@angular/core';
import { of } from 'rxjs';
import { WalletComponent } from './wallet.component';
import { PaymentService } from '../../services/payment.service';
import { TransactionService } from '../../services/transaction.service';
import { NotificationService } from '../../services/notification.service';
import { Card } from '../../models/card.model';
import { CardType } from '../../models/payment-method.model';

describe('WalletComponent', () => {
  let component: WalletComponent;
  let fixture: ComponentFixture<WalletComponent>;

  const cards: Card[] = [
    {
      id: 1,
      cardHolderName: 'Alice',
      cardType: CardType.VISA,
      lastFourDigits: '1234',
      expiryDate: '12/28',
      isDefault: true,
      paymentMethodType: 'DEBIT'
    }
  ];

  const paymentServiceMock = {
    getBalance: jasmine.createSpy().and.returnValue(of({ id: 1, balance: 100, currency: 'USD' })),
    getCards: jasmine.createSpy().and.returnValue(of(cards)),
    addFunds: jasmine.createSpy().and.returnValue(of({})),
    withdrawFunds: jasmine.createSpy().and.returnValue(of({}))
  };

  const transactionServiceMock = {
    sendMoney: jasmine.createSpy().and.returnValue(of({}))
  };

  const notificationServiceMock = {
    refreshUnreadCount: jasmine.createSpy()
  };

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [ReactiveFormsModule],
      declarations: [WalletComponent],
      providers: [
        { provide: PaymentService, useValue: paymentServiceMock },
        { provide: TransactionService, useValue: transactionServiceMock },
        { provide: NotificationService, useValue: notificationServiceMock }
      ],
      schemas: [NO_ERRORS_SCHEMA]
    });

    fixture = TestBed.createComponent(WalletComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should load wallet and cards on init', () => {
    expect(paymentServiceMock.getBalance).toHaveBeenCalled();
    expect(paymentServiceMock.getCards).toHaveBeenCalled();
    expect(component.wallet?.balance).toBe(100);
    expect(component.filteredFundCards.length).toBeGreaterThanOrEqual(1);
  });

  it('should send money with validated form and pin', () => {
    component.sendMoneyForm.setValue({ receiverEmail: 'bob@example.com', amount: 20, description: 'Test' });
    component.onSendMoney();
    expect(component.showPinModal).toBeTrue();

    component.onPinVerified('1234');
    expect(transactionServiceMock.sendMoney).toHaveBeenCalledWith({
      receiverEmail: 'bob@example.com',
      amount: 20,
      pin: '1234',
      description: 'Test'
    });
  });

  it('should add funds when form is valid', () => {
    component.activeTab = 'add';
    component.fundForm.setValue({ amount: 50, paymentMethodType: 'DEFAULT', cardId: cards[0].id });
    component.onAddFunds();
    expect(component.showPinModal).toBeTrue();
    component.onPinVerified('0000');
    expect(paymentServiceMock.addFunds).toHaveBeenCalledWith(50, cards[0].id);
  });

  it('should withdraw funds when form is valid', () => {
    component.activeTab = 'withdraw';
    component.withdrawForm.setValue({ amount: 30 });
    component.onWithdrawFunds();
    expect(component.showPinModal).toBeTrue();
    component.onPinVerified('0000');
    expect(paymentServiceMock.withdrawFunds).toHaveBeenCalledWith(30);
  });
});
