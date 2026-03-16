import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { MoneyRequestService, MoneyRequest } from '../../services/money-request.service';
import { NotificationService } from '../../services/notification.service';

@Component({
  selector: 'app-money-requests',
  templateUrl: './money-requests.component.html',
  styleUrls: ['./money-requests.component.css']
})
export class MoneyRequestsComponent implements OnInit {
  requestForm: FormGroup;
  requests: MoneyRequest[] = [];
  incomingRequests: MoneyRequest[] = [];
  outgoingRequests: MoneyRequest[] = [];
  loading = false;
  submitting = false;
  message = '';
  error = '';
  showPinModal = false;
  pendingRequestId: number | null = null;

  constructor(
    private fb: FormBuilder,
    private moneyRequestService: MoneyRequestService,
    private notificationService: NotificationService
  ) {
    this.requestForm = this.fb.group({
      payerEmail: ['', [Validators.required, Validators.email]],
      amount: [null, [Validators.required, Validators.min(0.01)]],
      note: ['', Validators.required]
    });
  }

  ngOnInit(): void {
    this.loadRequests();
  }

  loadRequests(): void {
    this.loading = true;
    this.moneyRequestService.getMyRequests().subscribe({
      next: (requests) => {
        this.requests = requests;
        this.incomingRequests = requests.filter(r => r.direction === 'INCOMING');
        this.outgoingRequests = requests.filter(r => r.direction === 'OUTGOING');
        this.error = '';
        this.loading = false;
      },
      error: (err) => {
        console.error('Error loading requests:', err);
        this.error = err.error?.message || 'Failed to load requests';
        this.loading = false;
      }
    });
  }

  onSubmit(): void {
    if (this.requestForm.invalid) return;

    this.submitting = true;
    this.message = '';
    this.error = '';

    this.moneyRequestService.createRequest(this.requestForm.value).subscribe({
      next: () => {
        this.message = 'Money request sent successfully!';
        this.requestForm.reset();
        this.loadRequests();
        this.notificationService.refreshUnreadCount();
        this.submitting = false;
      },
      error: (err) => {
        this.error = err.error?.message || 'Failed to send money request';
        this.submitting = false;
      }
    });
  }

  respond(requestId: number, accept: boolean): void {
    if (accept) {
      this.pendingRequestId = requestId;
      this.showPinModal = true;
    } else {
      this.executeResponse(requestId, false);
    }
  }

  onPinVerified(pin: string): void {
    this.showPinModal = false;
    if (this.pendingRequestId !== null) {
      this.executeResponse(this.pendingRequestId, true, pin);
      this.pendingRequestId = null;
    }
  }

  onPinCancel(): void {
    this.showPinModal = false;
    this.pendingRequestId = null;
  }

  private executeResponse(requestId: number, accept: boolean, pin?: string): void {
    this.moneyRequestService.respondToRequest(requestId, accept, pin).subscribe({
      next: () => {
        this.loadRequests();
        this.message = accept ? 'Request accepted!' : 'Request declined.';
        this.notificationService.refreshUnreadCount();
      },
      error: (err) => {
        this.error = err.error?.message || 'Failed to respond to request';
      }
    });
  }

  getDisplayEmail(email?: string): string {
    return email || 'Unknown user';
  }

  getStatusLabel(status: string): string {
    return (status || '').replace(/_/g, ' ');
  }
}
