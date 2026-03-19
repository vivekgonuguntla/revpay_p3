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
});
