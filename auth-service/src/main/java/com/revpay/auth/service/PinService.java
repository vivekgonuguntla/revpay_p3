package com.revpay.auth.service;

import com.revpay.auth.dto.PinResetRequest;
import com.revpay.auth.dto.PinSetupRequest;
import com.revpay.auth.dto.PinVerifyRequest;
import com.revpay.auth.dto.SecurityQuestionDto;
import com.revpay.auth.entity.User;
import com.revpay.auth.exception.InvalidCredentialsException;
import com.revpay.auth.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PinService {

    private static final Logger log = LoggerFactory.getLogger(PinService.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public PinService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public void setupPin(String username, PinSetupRequest request) {
        log.info("Setting up transaction PIN for user: {}", username);
        User user = userRepository.findByUsername(username)
                .or(() -> userRepository.findByEmail(username))
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        user.setTransactionPin(passwordEncoder.encode(request.getPin()));
        userRepository.save(user);
        log.info("Transaction PIN set successfully for user: {}", username);
    }

    public boolean verifyPin(String username, PinVerifyRequest request) {
        log.info("Verifying transaction PIN for user: {}", username);
        User user = userRepository.findByUsername(username)
                .or(() -> userRepository.findByEmail(username))
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        if (user.getTransactionPin() == null) {
            throw new InvalidCredentialsException("Transaction PIN not set");
        }

        boolean isValid = passwordEncoder.matches(request.getPin(), user.getTransactionPin());
        log.info("PIN verification result for user {}: {}", username, isValid);
        return isValid;
    }

    @Transactional
    public void resetPin(PinResetRequest request) {
        log.info("Resetting transaction PIN for user: {}", request.getEmail());
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        // Verify security answers
        if (user.getSecurityQuestions().size() != request.getAnswers().size()) {
            throw new InvalidCredentialsException("Question/Answer count mismatch");
        }

        for (int i = 0; i < user.getSecurityQuestions().size(); i++) {
            String storedAnswer = user.getSecurityQuestions().get(i).getAnswer();
            SecurityQuestionDto providedAnswer = request.getAnswers().get(i);
            if (!storedAnswer.equalsIgnoreCase(providedAnswer.getAnswer())) {
                throw new InvalidCredentialsException("Incorrect answers to security questions");
            }
        }

        // Reset PIN
        user.setTransactionPin(passwordEncoder.encode(request.getNewPin()));
        userRepository.save(user);
        log.info("Transaction PIN reset successfully for user: {}", request.getEmail());
    }
}
