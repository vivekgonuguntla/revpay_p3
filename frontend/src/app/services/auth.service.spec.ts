import { HttpClient } from '@angular/common/http';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { environment } from '../../environments/environment';
import { AuthRequest, AuthResponse, RegisterRequest, SecurityQuestionDto } from '../models/auth.model';
import { AuthService } from './auth.service';

describe('AuthService', () => {
  let service: AuthService;
  let httpMock: HttpTestingController;
  let router: Router;

  const authApiUrl = `${environment.apiUrl.replace(/\/v1$/, '')}/auth`;

  const securityQuestions: SecurityQuestionDto[] = [
    { question: 'What was the name of your first pet?', answer: 'Milo' }
  ];

  const authResponse: AuthResponse = {
    token: createToken({ sub: 'alice', userId: 42, role: 'BUSINESS' }),
    userId: 42,
    username: 'alice',
    email: 'alice@example.com',
    role: 'BUSINESS'
  };

  beforeEach(() => {
    localStorage.clear();

    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule],
      providers: [AuthService]
    });

    service = TestBed.inject(AuthService);
    httpMock = TestBed.inject(HttpTestingController);
    router = TestBed.inject(Router);
  });

  afterEach(() => {
    httpMock.verify();
    localStorage.clear();
  });

  it('should create', () => {
    expect(service).toBeTruthy();
  });

  it('should login, store auth data, and update current user', () => {
    const credentials: AuthRequest = {
      email: 'alice@example.com',
      password: 'secret123'
    };

    let responseBody: AuthResponse | undefined;
    service.login(credentials).subscribe(response => {
      responseBody = response;
    });

    const req = httpMock.expectOne(`${authApiUrl}/login`);
    expect(req.request.method).toBe('POST');
    expect(req.request.body).toEqual(credentials);
    req.flush(authResponse);

    expect(responseBody).toEqual(authResponse);
    expect(localStorage.getItem('token')).toBe(authResponse.token);
    expect(localStorage.getItem('userId')).toBe('42');
    expect(localStorage.getItem('username')).toBe('alice');
    expect(localStorage.getItem('email')).toBe('alice@example.com');
    expect(localStorage.getItem('role')).toBe('BUSINESS');
    expect(service.getCurrentUser()).toEqual({
      token: authResponse.token,
      userId: 42,
      username: 'alice',
      email: 'alice@example.com',
      role: 'BUSINESS'
    });
  });

  it('should register, store auth data, and expose authenticated state', () => {
    const registerRequest: RegisterRequest = {
      fullName: 'Alice Doe',
      username: 'alice',
      email: 'alice@example.com',
      password: 'secret123',
      phoneNumber: '9999999999',
      role: 'BUSINESS',
      securityQuestions
    };

    service.register(registerRequest).subscribe();

    const req = httpMock.expectOne(`${authApiUrl}/register`);
    expect(req.request.method).toBe('POST');
    expect(req.request.body).toEqual(registerRequest);
    req.flush(authResponse);

    expect(service.isAuthenticated()).toBeTrue();
    expect(service.isBusinessAccount()).toBeTrue();
    expect(service.getUsername()).toBe('alice');
    expect(service.getUserId()).toBe('42');
  });

  it('should restore current user from localStorage and token userId on startup', () => {
    localStorage.setItem('token', createToken({ sub: 'alice', userId: 77 }));
    localStorage.setItem('username', 'alice');
    localStorage.setItem('email', 'alice@example.com');
    localStorage.setItem('role', 'PERSONAL');

    const restoredService = new AuthService(TestBed.inject(HttpClient), router);

    expect(restoredService.getCurrentUser()).toEqual({
      token: localStorage.getItem('token')!,
      userId: 77,
      username: 'alice',
      email: 'alice@example.com',
      role: 'PERSONAL'
    });
    expect(localStorage.getItem('userId')).toBe('77');
  });

  it('should extract userId from token when userId is not already stored', () => {
    localStorage.setItem('token', createToken({ sub: 'alice', userId: 15 }));

    expect(service.getUserId()).toBe('15');
    expect(localStorage.getItem('userId')).toBe('15');
  });

  it('should logout, clear storage, and navigate to login', () => {
    const navigateSpy = spyOn(router, 'navigate').and.resolveTo(true);
    localStorage.setItem('token', authResponse.token);
    localStorage.setItem('userId', '42');
    localStorage.setItem('username', 'alice');
    localStorage.setItem('email', 'alice@example.com');
    localStorage.setItem('role', 'BUSINESS');

    service.logout();

    expect(service.getCurrentUser()).toBeNull();
    expect(localStorage.getItem('token')).toBeNull();
    expect(localStorage.getItem('userId')).toBeNull();
    expect(localStorage.getItem('username')).toBeNull();
    expect(localStorage.getItem('email')).toBeNull();
    expect(localStorage.getItem('role')).toBeNull();
    expect(navigateSpy).toHaveBeenCalledWith(['/login']);
  });
});

function createToken(payload: Record<string, unknown>): string {
  const encode = (value: object) =>
    btoa(JSON.stringify(value)).replace(/\+/g, '-').replace(/\//g, '_').replace(/=+$/, '');

  return `${encode({ alg: 'HS256', typ: 'JWT' })}.${encode(payload)}.signature`;
}
