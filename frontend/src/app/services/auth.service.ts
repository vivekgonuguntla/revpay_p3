import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BehaviorSubject, Observable, tap } from 'rxjs';
import { Router } from '@angular/router';
import { AuthRequest, AuthResponse, RegisterRequest, User, SecurityQuestionDto } from '../models/auth.model';

import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  isLoggedIn() {
    throw new Error('Method not implemented.');
  }
  private apiUrl = `${environment.apiUrl.replace(/\/v1$/, '')}/auth`;
  private currentUserSubject = new BehaviorSubject<User | null>(null);
  public currentUser$ = this.currentUserSubject.asObservable();

  constructor(private http: HttpClient, private router: Router) {
    const token = localStorage.getItem('token');
    const storedUserId = localStorage.getItem('userId');
    const username = localStorage.getItem('username');
    const email = localStorage.getItem('email');
    const role = localStorage.getItem('role');
    const userId = storedUserId ?? this.extractUserIdFromToken(token);

    if (token && username && role) {
      if (userId) {
        localStorage.setItem('userId', userId);
      }
      this.currentUserSubject.next({
        token,
        userId: userId ? Number(userId) : undefined,
        username,
        role,
        email: email ?? ''
      });
    }
  }

  register(user: RegisterRequest): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(`${this.apiUrl}/register`, user).pipe(
      tap(response => {
        this.storeAuthData(response);
      })
    );
  }

  login(credentials: AuthRequest): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(`${this.apiUrl}/login`, credentials).pipe(
      tap(response => {
        this.storeAuthData(response);
      })
    );
  }

  private storeAuthData(response: AuthResponse): void {
    localStorage.setItem('token', response.token);
    localStorage.setItem('userId', String(response.userId));
    localStorage.setItem('username', response.username);
    localStorage.setItem('email', response.email);
    localStorage.setItem('role', response.role);
    this.currentUserSubject.next({
      token: response.token,
      userId: response.userId,
      username: response.username,
      role: response.role,
      email: response.email
    });
  }

  logout(): void {
    localStorage.removeItem('token');
    localStorage.removeItem('userId');
    localStorage.removeItem('username');
    localStorage.removeItem('email');
    localStorage.removeItem('role');
    this.currentUserSubject.next(null);
    this.router.navigate(['/login']);
  }

  getToken(): string | null {
    return localStorage.getItem('token');
  }

  isAuthenticated(): boolean {
    return !!this.getToken();
  }

  getCurrentUser(): User | null {
    return this.currentUserSubject.value;
  }

  isBusinessAccount(): boolean {
    const role = localStorage.getItem('role');
    return role === 'BUSINESS';
  }

  getUsername(): string | null {
    return localStorage.getItem('username');
  }

  getUserId(): string | null {
    const storedUserId = localStorage.getItem('userId');
    if (storedUserId) {
      return storedUserId;
    }

    const token = this.getToken();
    const tokenUserId = this.extractUserIdFromToken(token);
    if (tokenUserId) {
      localStorage.setItem('userId', tokenUserId);
    }
    return tokenUserId;
  }

  private extractUserIdFromToken(token: string | null): string | null {
    if (!token) {
      return null;
    }

    try {
      const payload = token.split('.')[1];
      if (!payload) {
        return null;
      }

      const normalized = payload.replace(/-/g, '+').replace(/_/g, '/');
      const padded = normalized.padEnd(Math.ceil(normalized.length / 4) * 4, '=');
      const decoded = JSON.parse(atob(padded)) as { userId?: string | number };

      if (decoded.userId === undefined || decoded.userId === null) {
        return null;
      }

      return String(decoded.userId);
    } catch {
      return null;
    }
  }

  // Security & Recovery
  setTransactionPin(pin: string): Observable<any> {
    return this.http.post(`${environment.apiUrl}/security/pin/setup`, { pin });
  }

  verifyTransactionPin(pin: string): Observable<{ valid: boolean }> {
    return this.http.post<{ valid: boolean }>(`${environment.apiUrl}/security/pin/verify`, { pin });
  }

  verify2fa(code: string): Observable<{ verified: boolean }> {
    return this.http.post<{ verified: boolean }>(`${environment.apiUrl}/security/2fa/verify`, { code });
  }

  getRecoveryQuestions(email: string): Observable<string[]> {
    return this.http.get<string[]>(`${this.apiUrl}/recovery/questions?email=${email}`);
  }

  resetPasswordWithQuestions(email: string, answers: SecurityQuestionDto[], newPassword: string): Observable<any> {
    return this.http.post(`${this.apiUrl}/recovery/reset`, { email, answers, newPassword });
  }

  resetTransactionPinWithQuestions(email: string, answers: SecurityQuestionDto[], newPin: string): Observable<any> {
    return this.http.post(`${environment.apiUrl}/security/pin/reset`, { email, answers, newPin });
  }
}
