import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { WalletService } from './wallet.service';
import { environment } from '../../environments/environment';
import { Wallet } from '../models/wallet.model';

describe('WalletService', () => {
  let service: WalletService;
  let httpMock: HttpTestingController;
  const apiUrl = `${environment.apiUrl}/wallet`;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule]
    });
    service = TestBed.inject(WalletService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => httpMock.verify());

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('getBalance should call GET /wallet/balance', () => {
    const mockWallet: Wallet = { id: 1, balance: 250.75, currency: 'USD' };

    service.getBalance().subscribe((wallet) => {
      expect(wallet).toEqual(mockWallet);
    });

    const req = httpMock.expectOne(`${apiUrl}/balance`);
    expect(req.request.method).toBe('GET');
    req.flush(mockWallet);
  });
});
