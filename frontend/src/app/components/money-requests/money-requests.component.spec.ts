import { ComponentFixture, TestBed } from '@angular/core/testing';
import { NO_ERRORS_SCHEMA } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import { of } from 'rxjs';
import { MoneyRequestsComponent } from './money-requests.component';
import { MoneyRequestService, MoneyRequest } from '../../services/money-request.service';
import { NotificationService } from '../../services/notification.service';

describe('MoneyRequestsComponent', () => {
  let component: MoneyRequestsComponent;
  let fixture: ComponentFixture<MoneyRequestsComponent>;

  const mockRequests: MoneyRequest[] = [
    {
      id: 1,
      requesterId: 2,
      payerId: 3,
      requesterEmail: 'alice@example.com',
      payerEmail: 'bob@example.com',
      amount: 20,
      note: 'Lunch',
      status: 'PENDING',
      direction: 'INCOMING',
      createdAt: '2026-02-26T10:00:00'
    },
    {
      id: 2,
      requesterId: 3,
      payerId: 2,
      requesterEmail: 'bob@example.com',
      payerEmail: 'alice@example.com',
      amount: 15,
      note: 'Coffee',
      status: 'ACCEPTED',
      direction: 'OUTGOING',
      createdAt: '2026-02-25T09:00:00'
    }
  ];

  const moneyRequestServiceMock = {
    getMyRequests: jasmine.createSpy().and.returnValue(of(mockRequests)),
    createRequest: jasmine.createSpy().and.returnValue(of({ id: 99 })),
    respondToRequest: jasmine.createSpy().and.returnValue(of({ success: true }))
  };

  const notificationServiceMock = {
    refreshUnreadCount: jasmine.createSpy()
  };

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [ReactiveFormsModule],
      declarations: [MoneyRequestsComponent],
      providers: [
        { provide: MoneyRequestService, useValue: moneyRequestServiceMock },
        { provide: NotificationService, useValue: notificationServiceMock }
      ],
      schemas: [NO_ERRORS_SCHEMA]
    });

    fixture = TestBed.createComponent(MoneyRequestsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should load and split incoming/outgoing requests', () => {
    expect(moneyRequestServiceMock.getMyRequests).toHaveBeenCalled();
    expect(component.incomingRequests.length).toBe(1);
    expect(component.outgoingRequests.length).toBe(1);
  });

  it('should submit a new money request', () => {
    component.requestForm.setValue({ payerEmail: 'joe@example.com', amount: 10, note: 'Test' });
    component.onSubmit();
    expect(moneyRequestServiceMock.createRequest).toHaveBeenCalledWith({
      payerEmail: 'joe@example.com',
      amount: 10,
      note: 'Test'
    });
  });

  it('should handle accepting a request via PIN', () => {
    component.respond(1, true);
    expect(component.showPinModal).toBeTrue();
    component.onPinVerified('1234');
    expect(moneyRequestServiceMock.respondToRequest).toHaveBeenCalledWith(1, true, '1234');
  });

  it('should decline a request without PIN', () => {
    component.respond(1, false);
    expect(moneyRequestServiceMock.respondToRequest).toHaveBeenCalledWith(1, false, undefined);
  });
});
