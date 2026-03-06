package com.revpay.business.service;

import com.revpay.business.dto.BankAccountRequest;
import com.revpay.business.dto.BankAccountResponse;
import com.revpay.business.entity.BusinessBankAccount;
import com.revpay.business.repository.BusinessBankAccountRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BankAccountService {
    private final BusinessBankAccountRepository bankAccountRepository;
    private static final String ENCRYPTION_KEY = "RevPay2024Secret"; // In production, use proper key management

    public BankAccountService(BusinessBankAccountRepository bankAccountRepository) {
        this.bankAccountRepository = bankAccountRepository;
    }

    @Transactional
    public BankAccountResponse addBankAccount(Long userId, BankAccountRequest request) {
        // If this is set as default, clear other defaults
        if (request.isDefault()) {
            bankAccountRepository.clearDefaultForUser(userId);
        }

        BusinessBankAccount account = BusinessBankAccount.builder()
                .userId(userId)
                .bankName(request.getBankName())
                .accountHolderName(request.getAccountHolderName())
                .encryptedAccountNumber(encrypt(request.getAccountNumber()))
                .accountLastFour(getLastFour(request.getAccountNumber()))
                .encryptedRoutingNumber(encrypt(request.getRoutingNumber()))
                .accountType(request.getAccountType())
                .isDefault(request.isDefault())
                .build();

        BusinessBankAccount saved = bankAccountRepository.save(account);
        return mapToResponse(saved);
    }

    public List<BankAccountResponse> getBankAccountsByUserId(Long userId) {
        return bankAccountRepository.findByUserId(userId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public void deleteBankAccount(Long accountId, Long userId) {
        BusinessBankAccount account = bankAccountRepository.findById(accountId)
                .orElseThrow(() -> new RuntimeException("Bank account not found"));

        if (!account.getUserId().equals(userId)) {
            throw new RuntimeException("Unauthorized to delete this account");
        }

        bankAccountRepository.delete(account);
    }

    @Transactional
    public BankAccountResponse setDefaultAccount(Long accountId, Long userId) {
        BusinessBankAccount account = bankAccountRepository.findById(accountId)
                .orElseThrow(() -> new RuntimeException("Bank account not found"));

        if (!account.getUserId().equals(userId)) {
            throw new RuntimeException("Unauthorized to modify this account");
        }

        bankAccountRepository.clearDefaultForUser(userId);
        account.setDefault(true);
        BusinessBankAccount saved = bankAccountRepository.save(account);
        return mapToResponse(saved);
    }

    private String encrypt(String value) {
        try {
            SecretKeySpec key = new SecretKeySpec(ENCRYPTION_KEY.getBytes(StandardCharsets.UTF_8), "AES");
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE, key);
            byte[] encrypted = cipher.doFinal(value.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(encrypted);
        } catch (Exception e) {
            throw new RuntimeException("Encryption failed", e);
        }
    }

    private String getLastFour(String accountNumber) {
        if (accountNumber == null || accountNumber.length() < 4) {
            return accountNumber;
        }
        return accountNumber.substring(accountNumber.length() - 4);
    }

    private BankAccountResponse mapToResponse(BusinessBankAccount account) {
        return BankAccountResponse.builder()
                .id(account.getId())
                .bankName(account.getBankName())
                .accountHolderName(account.getAccountHolderName())
                .accountLastFour(account.getAccountLastFour())
                .accountType(account.getAccountType())
                .isDefault(account.isDefault())
                .createdAt(account.getCreatedAt())
                .build();
    }
}
