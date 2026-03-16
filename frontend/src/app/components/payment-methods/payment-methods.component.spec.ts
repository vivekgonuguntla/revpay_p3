import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ReactiveFormsModule } from '@angular/forms';
import { NO_ERRORS_SCHEMA } from '@angular/core';
import { of } from 'rxjs';
import { PaymentMethodsComponent } from './payment-methods.component';
import { PaymentService } from '../../services/payment.service';
import { NotificationService } from '../../services/notification.service';

describe('PaymentMethodsComponent', () => {
  let component: PaymentMethodsComponent;
  let fixture: ComponentFixture<PaymentMethodsComponent>;

  const paymentServiceMock = {
    getCards: jasmine.createSpy().and.returnValue(of([])),
    addCard: jasmine.createSpy().and.returnValue(of({
      id: 1,
      cardHolderName: 'Alice',
      lastFourDigits: '1234',
      expiryDate: '12/28',
      cardType: 'VISA',
      paymentMethodType: 'DEBIT',
      isDefault: true
    })),
    setDefaultCard: jasmine.createSpy().and.returnValue(of({}))
  };
  const notificationServiceMock = { refreshUnreadCount: jasmine.createSpy() };

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [ReactiveFormsModule],
      declarations: [PaymentMethodsComponent],
      providers: [
        { provide: PaymentService, useValue: paymentServiceMock },
        { provide: NotificationService, useValue: notificationServiceMock }
      ],
      schemas: [NO_ERRORS_SCHEMA]
    });
    fixture = TestBed.createComponent(PaymentMethodsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
