package com.revpay.business.service;

import com.revpay.business.dto.BankAccountRequest;
import com.revpay.business.dto.BankAccountResponse;
import com.revpay.business.entity.BusinessBankAccount;
import com.revpay.business.repository.BusinessBankAccountRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BankAccountServiceTest {

    @Mock
    private BusinessBankAccountRepository bankAccountRepository;

    @InjectMocks
    private BankAccountService bankAccountService;

    @Test
    void addBankAccountClearsPreviousDefaultAndEncryptsSensitiveFields() {
        BankAccountRequest request = new BankAccountRequest();
        request.setBankName("Rev Bank");
        request.setAccountHolderName("Nikhil");
        request.setAccountNumber("1234567890");
        request.setRoutingNumber("987654321");
        request.setAccountType("CHECKING");
        request.setDefault(true);

        when(bankAccountRepository.save(any(BusinessBankAccount.class))).thenAnswer(invocation -> {
            BusinessBankAccount account = invocation.getArgument(0);
            account.setId(3L);
            account.setCreatedAt(LocalDateTime.now());
            return account;
        });

        BankAccountResponse response = bankAccountService.addBankAccount(8L, request);

        ArgumentCaptor<BusinessBankAccount> captor = ArgumentCaptor.forClass(BusinessBankAccount.class);
        verify(bankAccountRepository).clearDefaultForUser(8L);
        verify(bankAccountRepository).save(captor.capture());
        BusinessBankAccount saved = captor.getValue();
        assertEquals("7890", saved.getAccountLastFour());
        assertNotEquals("1234567890", saved.getEncryptedAccountNumber());
        assertNotEquals("987654321", saved.getEncryptedRoutingNumber());
        assertEquals(3L, response.getId());
    }

    @Test
    void deleteBankAccountThrowsForAnotherUsersAccount() {
        BusinessBankAccount account = BusinessBankAccount.builder()
                .id(3L)
                .userId(99L)
                .build();

        when(bankAccountRepository.findById(3L)).thenReturn(Optional.of(account));

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> bankAccountService.deleteBankAccount(3L, 8L));

        assertEquals("Unauthorized to delete this account", exception.getMessage());
    }

    @Test
    void setDefaultAccountClearsOldDefaultAndSavesUpdatedAccount() {
        BusinessBankAccount account = BusinessBankAccount.builder()
                .id(3L)
                .userId(8L)
                .isDefault(false)
                .build();

        when(bankAccountRepository.findById(3L)).thenReturn(Optional.of(account));
        when(bankAccountRepository.save(account)).thenReturn(account);

        BankAccountResponse response = bankAccountService.setDefaultAccount(3L, 8L);

        verify(bankAccountRepository).clearDefaultForUser(8L);
        verify(bankAccountRepository).save(account);
        assertEquals(true, account.isDefault());
        assertEquals(true, response.isDefault());
    }

    @Test
    void addBankAccountDoesNotClearDefaultsWhenRequestIsNotDefault() {
        BankAccountRequest request = new BankAccountRequest();
        request.setBankName("Rev Bank");
        request.setAccountHolderName("Nikhil");
        request.setAccountNumber("1234567890");
        request.setRoutingNumber("987654321");
        request.setAccountType("CHECKING");
        request.setDefault(false);

        when(bankAccountRepository.save(any(BusinessBankAccount.class))).thenAnswer(invocation -> invocation.getArgument(0));

        bankAccountService.addBankAccount(8L, request);

        verify(bankAccountRepository, never()).clearDefaultForUser(8L);
    }
}
