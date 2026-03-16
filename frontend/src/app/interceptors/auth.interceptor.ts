import { Injectable } from '@angular/core';
import { HttpRequest, HttpHandler, HttpEvent, HttpInterceptor } from '@angular/common/http';
import { Observable } from 'rxjs';
import { AuthService } from '../services/auth.service';

@Injectable()
export class AuthInterceptor implements HttpInterceptor {
    constructor(private authService: AuthService) { }

    intercept(request: HttpRequest<unknown>, next: HttpHandler): Observable<HttpEvent<unknown>> {

        const token = this.authService.getToken();
        const userId = this.authService.getUserId();
        const isAuthEndpoint = request.url.includes('/auth/');

        if (token && !isAuthEndpoint) {
            const headers: Record<string, string> = {
                Authorization: `Bearer ${token}`
            };

            if (userId) {
                headers['X-User-Id'] = userId;
            }

            request = request.clone({
                setHeaders: headers
            });
        }

        return next.handle(request);
    }
}
