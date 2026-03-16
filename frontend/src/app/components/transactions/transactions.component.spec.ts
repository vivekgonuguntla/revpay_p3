import { ComponentFixture, TestBed } from '@angular/core/testing';
import { of } from 'rxjs';
import { NO_ERRORS_SCHEMA } from '@angular/core';
import { TransactionsComponent } from './transactions.component';
import { TransactionService } from '../../services/transaction.service';
import { Transaction } from '../../models/transaction.model';

describe('TransactionsComponent', () => {
  let component: TransactionsComponent;
  let fixture: ComponentFixture<TransactionsComponent>;

  const transactions: Transaction[] = [
    {
      id: 1,
      senderEmail: 'alice@example.com',
      receiverEmail: 'bob@example.com',
      amount: 50,
      type: 'SEND',
      status: 'COMPLETED',
      description: 'Groceries',
      timestamp: '2026-02-26T10:00:00'
    }
  ];

  const transactionServiceMock = {
    getTransactions: jasmine.createSpy().and.returnValue(of(transactions))
  };

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [TransactionsComponent],
      providers: [
        { provide: TransactionService, useValue: transactionServiceMock }
      ],
      schemas: [NO_ERRORS_SCHEMA]
    });
    fixture = TestBed.createComponent(TransactionsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should load transactions on init', () => {
    expect(transactionServiceMock.getTransactions).toHaveBeenCalled();
    expect(component.transactions).toEqual(transactions);
    expect(component.loading).toBeFalse();
  });

  it('should compute transaction class and sign correctly', () => {
    const tx = transactions[0];
    expect(component.getTransactionClass(tx)).toBe('negative');
    expect(component.getTransactionSign(tx)).toBe('-');
  });
});
