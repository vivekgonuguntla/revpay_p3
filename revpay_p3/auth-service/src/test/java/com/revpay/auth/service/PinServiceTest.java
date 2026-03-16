package com.revpay.auth.service;

import com.revpay.auth.dto.PinResetRequest;
import com.revpay.auth.dto.PinSetupRequest;
import com.revpay.auth.dto.PinVerifyRequest;
import com.revpay.auth.dto.SecurityQuestionDto;
import com.revpay.auth.entity.Role;
import com.revpay.auth.entity.SecurityQuestion;
import com.revpay.auth.entity.User;
import com.revpay.auth.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PinServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private PinService pinService;

    private User user;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(1L)
                .username("alice")
                .email("alice@example.com")
                .password("encoded-secret")
                .fullName("Alice Doe")
                .role(Role.PERSONAL)
                .enabled(true)
                .securityQuestions(List.of(
                        SecurityQuestion.builder().question("Pet?").answer("Milo").build()
                ))
                .build();
    }

    @Test
    void setupPinShouldEncodeAndSavePin() {
        PinSetupRequest request = new PinSetupRequest("1234");
        when(userRepository.findByUsername("alice")).thenReturn(Optional.of(user));
        when(passwordEncoder.encode("1234")).thenReturn("encoded-pin");

        pinService.setupPin("alice", request);

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());
        assertEquals("encoded-pin", userCaptor.getValue().getTransactionPin());
    }

    @Test
    void verifyPinShouldReturnTrueForMatchingPin() {
        user.setTransactionPin("encoded-pin");
        PinVerifyRequest request = new PinVerifyRequest("1234");
        when(userRepository.findByUsername("alice")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("1234", "encoded-pin")).thenReturn(true);

        boolean result = pinService.verifyPin("alice", request);

        assertTrue(result);
    }

    @Test
    void resetPinShouldValidateAnswersEncodeAndSavePin() {
        PinResetRequest request = new PinResetRequest(
                "alice@example.com",
                "4321",
                List.of(new SecurityQuestionDto("Pet?", "milo"))
        );
        when(userRepository.findByEmail("alice@example.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.encode("4321")).thenReturn("encoded-new-pin");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        pinService.resetPin(request);

        assertEquals("encoded-new-pin", user.getTransactionPin());
        verify(userRepository).save(user);
    }
}
