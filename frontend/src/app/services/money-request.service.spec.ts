import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { MoneyRequestService, CreateMoneyRequestDto, MoneyRequest } from './money-request.service';
import { environment } from '../../environments/environment';

describe('MoneyRequestService', () => {
  let service: MoneyRequestService;
  let httpMock: HttpTestingController;
  const apiUrl = `${environment.apiUrl}/requests`;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule]
    });
    service = TestBed.inject(MoneyRequestService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => httpMock.verify());

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('createRequest should POST to /requests/create', () => {
    const dto: CreateMoneyRequestDto = { payerEmail: 'bob@example.com', amount: 15.5, note: 'Lunch' };
    const response = { id: 42 };

    service.createRequest(dto).subscribe(res => {
      expect(res).toEqual(response);
    });

    const req = httpMock.expectOne(`${apiUrl}/create`);
    expect(req.request.method).toBe('POST');
    expect(req.request.body).toEqual(dto);
    req.flush(response);
  });

  it('getMyRequests should GET /requests', () => {
    const requests: MoneyRequest[] = [
      {
        id: 1,
        requesterId: 2,
        payerId: 3,
        requesterEmail: 'alice@example.com',
        payerEmail: 'bob@example.com',
        amount: 25,
        note: 'Dinner',
        status: 'PENDING',
        direction: 'INCOMING',
        createdAt: '2026-02-26T10:00:00'
      }
    ];

    service.getMyRequests().subscribe(res => {
      expect(res).toEqual(requests);
    });

    const req = httpMock.expectOne(apiUrl);
    expect(req.request.method).toBe('GET');
    req.flush(requests);
  });

  it('respondToRequest should POST to /requests/:id/respond', () => {
    const requestId = 7;
    const payload = { accept: true, pin: '1234' };

    service.respondToRequest(requestId, true, '1234').subscribe(res => {
      expect(res).toEqual({ success: true });
    });

    const req = httpMock.expectOne(`${apiUrl}/${requestId}/respond`);
    expect(req.request.method).toBe('POST');
    expect(req.request.body).toEqual(payload);
    req.flush({ success: true });
  });
});
