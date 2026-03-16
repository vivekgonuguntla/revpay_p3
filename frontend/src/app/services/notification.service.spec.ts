import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { environment } from '../../environments/environment';
import { NotificationService } from './notification.service';

describe('NotificationService', () => {
  let service: NotificationService;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule]
    });

    service = TestBed.inject(NotificationService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should request notifications with unread and category filters together', () => {
    service.getNotifications('REQUESTS', true).subscribe();

    const req = httpMock.expectOne(
      `${environment.apiUrl}/notifications?unreadOnly=true&category=REQUESTS`
    );
    expect(req.request.method).toBe('GET');
    req.flush([]);
  });

  it('should load notification preferences', () => {
    const response = {
      transactionsEnabled: true,
      requestsEnabled: false,
      alertsEnabled: true,
      lowBalanceThreshold: 250
    };

    service.getPreferences().subscribe(prefs => {
      expect(prefs).toEqual(response);
    });

    const req = httpMock.expectOne(`${environment.apiUrl}/notifications/preferences`);
    expect(req.request.method).toBe('GET');
    req.flush(response);
  });

  it('should update notification preferences', () => {
    const payload = {
      transactionsEnabled: false,
      requestsEnabled: true,
      alertsEnabled: true,
      lowBalanceThreshold: 75
    };

    service.updatePreferences(payload).subscribe(prefs => {
      expect(prefs.lowBalanceThreshold).toBe(75);
      expect(prefs.transactionsEnabled).toBeFalse();
    });

    const req = httpMock.expectOne(`${environment.apiUrl}/notifications/preferences`);
    expect(req.request.method).toBe('PUT');
    expect(req.request.body).toEqual(payload);
    req.flush(payload);
  });
});
