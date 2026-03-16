import { TestBed } from '@angular/core/testing';
import { ActivatedRouteSnapshot, Router, RouterStateSnapshot } from '@angular/router';

import { AuthGuard } from './auth.guard';
import { AuthService } from '../services/auth.service';

describe('AuthGuard', () => {
  let guard: AuthGuard;
  let authService: jasmine.SpyObj<AuthService>;
  let router: jasmine.SpyObj<Router>;

  beforeEach(() => {
    authService = jasmine.createSpyObj<AuthService>('AuthService', ['isAuthenticated']);
    router = jasmine.createSpyObj<Router>('Router', ['navigate']);
    router.navigate.and.resolveTo(true);

    TestBed.configureTestingModule({
      providers: [
        AuthGuard,
        { provide: AuthService, useValue: authService },
        { provide: Router, useValue: router }
      ]
    });

    guard = TestBed.inject(AuthGuard);
  });

  it('should allow navigation when the user is authenticated', () => {
    authService.isAuthenticated.and.returnValue(true);

    const result = guard.canActivate({} as ActivatedRouteSnapshot, { url: '/dashboard' } as RouterStateSnapshot);

    expect(result).toBeTrue();
    expect(router.navigate).not.toHaveBeenCalled();
  });

  it('should redirect to login with returnUrl when the user is not authenticated', () => {
    authService.isAuthenticated.and.returnValue(false);

    const result = guard.canActivate({} as ActivatedRouteSnapshot, { url: '/wallet' } as RouterStateSnapshot);

    expect(result).toBeFalse();
    expect(router.navigate).toHaveBeenCalledWith(['/login'], {
      queryParams: { returnUrl: '/wallet' }
    });
  });
});
