import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import {
    BusinessAnalyticsResponse,
    CreateInvoiceRequest,
    InvoiceResponse,
    LoanApplicationRequest,
    LoanResponse
} from '../models/business.model';

@Injectable({
    providedIn: 'root'
})
export class BusinessService {
    private readonly baseUrl = `${environment.apiUrl}/business`;

    constructor(private http: HttpClient) { }

    createInvoice(payload: CreateInvoiceRequest): Observable<InvoiceResponse> {
        return this.http.post<InvoiceResponse>(`${this.baseUrl}/invoices`, payload);
    }

    listInvoices(status?: string): Observable<InvoiceResponse[]> {
        if (!status) {
            return this.http.get<InvoiceResponse[]>(`${this.baseUrl}/invoices`);
        }
        return this.http.get<InvoiceResponse[]>(`${this.baseUrl}/invoices?status=${status}`);
    }

    lookupInvoices(type: string, value: string): Observable<InvoiceResponse[]> {
        return this.http.get<InvoiceResponse[]>(`${this.baseUrl}/invoices/lookup?type=${type}&value=${encodeURIComponent(value)}`);
    }

    applyLoan(payload: LoanApplicationRequest): Observable<LoanResponse> {
        return this.http.post<LoanResponse>(`${this.baseUrl}/loans`, payload);
    }

    listLoans(): Observable<LoanResponse[]> {
        return this.http.get<LoanResponse[]>(`${this.baseUrl}/loans`);
    }

    payInstallment(loanId: number, repaymentId: number): Observable<LoanResponse> {
        return this.http.post<LoanResponse>(`${this.baseUrl}/loans/${loanId}/repayments/${repaymentId}/pay`, {});
    }

    getAnalytics(from?: string, to?: string): Observable<BusinessAnalyticsResponse> {
        const queryParams: string[] = [];
        if (from) queryParams.push(`from=${from}`);
        if (to) queryParams.push(`to=${to}`);
        const query = queryParams.length ? `?${queryParams.join('&')}` : '';
        return this.http.get<BusinessAnalyticsResponse>(`${this.baseUrl}/analytics${query}`);
    }
}
