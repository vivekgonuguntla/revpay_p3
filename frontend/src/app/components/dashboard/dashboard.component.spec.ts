import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';
import { of } from 'rxjs';
import { DashboardComponent } from './dashboard.component';
import { AuthService } from '../../services/auth.service';
import { TransactionService } from '../../services/transaction.service';
import { PaymentService } from '../../services/payment.service';
import { NotificationService } from '../../services/notification.service';

describe('DashboardComponent', () => {
  let component: DashboardComponent;
  let fixture: ComponentFixture<DashboardComponent>;

  const authServiceMock = {
    getCurrentUser: () => ({ username: 'alice' }),
    logout: jasmine.createSpy(),
    isBusinessAccount: () => false
  };
  const transactionServiceMock = {
    getTransactions: jasmine.createSpy().and.returnValue(of([]))
  };
  const paymentServiceMock = {
    getBalance: jasmine.createSpy().and.returnValue(of({ id: 1, balance: 100, currency: 'USD' }))
  };
  const notificationServiceMock = {
    unreadCount$: of(0),
    refreshUnreadCount: jasmine.createSpy()
  };

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [RouterTestingModule],
      declarations: [DashboardComponent],
      providers: [
        { provide: AuthService, useValue: authServiceMock },
        { provide: TransactionService, useValue: transactionServiceMock },
        { provide: PaymentService, useValue: paymentServiceMock },
        { provide: NotificationService, useValue: notificationServiceMock }
      ]
    });
    fixture = TestBed.createComponent(DashboardComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
