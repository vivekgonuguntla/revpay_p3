import { Component, EventEmitter, Input, Output } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { of } from 'rxjs';
import { MoneyRequestService } from '../../services/money-request.service';
import { NotificationPreference, NotificationService } from '../../services/notification.service';
import { NotificationsComponent } from './notifications.component';

@Component({
  selector: 'app-feature-nav',
  template: ''
})
class FeatureNavStubComponent { }

@Component({
  selector: 'app-pin-verification',
  template: ''
})
class PinVerificationStubComponent {
  @Input() show = false;
  @Output() verifiedPin = new EventEmitter<string>();
  @Output() cancel = new EventEmitter<void>();
}

describe('NotificationsComponent', () => {
  let component: NotificationsComponent;
  let fixture: ComponentFixture<NotificationsComponent>;
  let notificationServiceSpy: jasmine.SpyObj<NotificationService>;
  let moneyRequestServiceSpy: jasmine.SpyObj<MoneyRequestService>;

  const routerSpy = jasmine.createSpyObj<Router>('Router', ['navigate', 'navigateByUrl']);
  const preferencesResponse: NotificationPreference = {
    transactionsEnabled: true,
    requestsEnabled: false,
    alertsEnabled: true,
    lowBalanceThreshold: 150
  };

  beforeEach(() => {
    notificationServiceSpy = jasmine.createSpyObj<NotificationService>(
      'NotificationService',
      ['getNotifications', 'refreshUnreadCount', 'markAsRead', 'markAllAsRead', 'getPreferences', 'updatePreferences']
    );
    moneyRequestServiceSpy = jasmine.createSpyObj<MoneyRequestService>(
      'MoneyRequestService',
      ['getMyRequests', 'respondToRequest']
    );

    notificationServiceSpy.getNotifications.and.returnValue(of([]));
    notificationServiceSpy.getPreferences.and.returnValue(of(preferencesResponse));
    notificationServiceSpy.updatePreferences.and.callFake((payload: Partial<NotificationPreference>) => of({
      transactionsEnabled: payload.transactionsEnabled ?? true,
      requestsEnabled: payload.requestsEnabled ?? true,
      alertsEnabled: payload.alertsEnabled ?? true,
      lowBalanceThreshold: payload.lowBalanceThreshold ?? 100
    }));
    notificationServiceSpy.markAsRead.and.returnValue(of({ message: 'ok' }));
    notificationServiceSpy.markAllAsRead.and.returnValue(of({ message: 'ok' }));
    moneyRequestServiceSpy.getMyRequests.and.returnValue(of([]));
    moneyRequestServiceSpy.respondToRequest.and.returnValue(of({}));

    TestBed.configureTestingModule({
      imports: [FormsModule],
      declarations: [
        NotificationsComponent,
        FeatureNavStubComponent,
        PinVerificationStubComponent
      ],
      providers: [
        { provide: NotificationService, useValue: notificationServiceSpy },
        { provide: MoneyRequestService, useValue: moneyRequestServiceSpy },
        { provide: Router, useValue: routerSpy }
      ]
    });

    fixture = TestBed.createComponent(NotificationsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should load preferences on init and render the preferences panel', () => {
    expect(notificationServiceSpy.getPreferences).toHaveBeenCalled();
    expect(component.preferences).toEqual(preferencesResponse);
    expect(fixture.nativeElement.textContent).toContain('Notification Preferences');
    expect(fixture.nativeElement.textContent).toContain('Requests');
  });

  it('should keep the fallback low balance threshold when backend returns null', () => {
    notificationServiceSpy.getPreferences.and.returnValue(of({
      transactionsEnabled: true,
      requestsEnabled: true,
      alertsEnabled: true,
      lowBalanceThreshold: null as unknown as number
    }));

    const newFixture = TestBed.createComponent(NotificationsComponent);
    const newComponent = newFixture.componentInstance;
    newFixture.detectChanges();

    expect(newComponent.preferences.lowBalanceThreshold).toBe(100);
  });

  it('should save notification preferences and show success feedback', () => {
    component.preferences = {
      transactionsEnabled: false,
      requestsEnabled: true,
      alertsEnabled: false,
      lowBalanceThreshold: 80
    };

    component.savePreferences();

    expect(notificationServiceSpy.updatePreferences).toHaveBeenCalledWith(component.preferences);
    expect(component.savingPreferences).toBeFalse();
    expect(component.successMessage).toBe('Notification preferences saved.');
  });

  it('should refresh unread count after marking all notifications as read', () => {
    component.notifications = [
      {
        id: 1,
        category: 'ALERTS',
        type: 'LOW_BALANCE',
        title: 'Low balance',
        message: 'Balance dropped',
        read: false,
        createdAt: '2026-03-16T10:00:00'
      }
    ];

    component.markAllAsRead();

    expect(notificationServiceSpy.markAllAsRead).toHaveBeenCalled();
    expect(notificationServiceSpy.refreshUnreadCount).toHaveBeenCalled();
    expect(component.notifications.every(item => item.read)).toBeTrue();
  });

  it('should reflect latest request status in request notifications', () => {
    notificationServiceSpy.getNotifications.and.returnValue(of([
      {
        id: 11,
        category: 'REQUESTS',
        type: 'MONEY_REQUEST_SENT',
        title: 'Request Sent',
        message: 'Money request sent',
        status: 'PENDING',
        navigationTarget: '/requests/44',
        read: false,
        createdAt: '2026-03-16T10:00:00'
      }
    ]));
    moneyRequestServiceSpy.getMyRequests.and.returnValue(of([
      {
        id: 44,
        requesterId: 1,
        payerId: 2,
        amount: 50,
        note: 'Lunch',
        status: 'ACCEPTED',
        direction: 'OUTGOING',
        createdAt: '2026-03-16T10:00:00'
      }
    ]));

    const newFixture = TestBed.createComponent(NotificationsComponent);
    const newComponent = newFixture.componentInstance;
    newFixture.detectChanges();

    expect(newComponent.notifications[0].status).toBe('ACCEPTED');
  });

  it('should only allow response actions for pending received request notifications', () => {
    const pendingReceived = {
      id: 21,
      category: 'REQUESTS' as const,
      type: 'MONEY_REQUEST_RECEIVED',
      title: 'Request Received',
      message: 'Please pay',
      status: 'PENDING',
      navigationTarget: '/requests/77',
      read: false,
      createdAt: '2026-03-16T10:00:00'
    };

    const acceptedSent = {
      ...pendingReceived,
      id: 22,
      type: 'MONEY_REQUEST_ACCEPTED',
      status: 'ACCEPTED'
    };

    component['requestStatusById'] = { 77: 'PENDING' };
    expect(component.canRespondToNotification(pendingReceived)).toBeTrue();

    component['requestStatusById'] = { 77: 'ACCEPTED' };
    expect(component.canRespondToNotification(pendingReceived)).toBeFalse();
    expect(component.canRespondToNotification(acceptedSent)).toBeFalse();
  });
});
