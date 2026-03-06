package com.revpay.auth.config;

import com.revpay.auth.entity.Role;
import com.revpay.auth.entity.SecurityQuestion;
import com.revpay.auth.entity.User;
import com.revpay.auth.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class DataLoader implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataLoader.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public DataLoader(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        if (userRepository.count() == 0) {
            log.info("Loading initial data...");
            loadUsers();
            log.info("Initial data loaded successfully");
        }
    }

    private void loadUsers() {
        // Admin user
        User admin = User.builder()
                .username("admin")
                .email("admin@revpay.com")
                .password(passwordEncoder.encode("admin123"))
                .fullName("Admin User")
                .phoneNumber("1234567890")
                .role(Role.ADMIN)
                .enabled(true)
                .failedLoginAttempts(0)
                .build();

        List<SecurityQuestion> adminQuestions = new ArrayList<>();
        adminQuestions.add(SecurityQuestion.builder()
                .user(admin)
                .question("What is your favorite color?")
                .answer("blue")
                .build());
        adminQuestions.add(SecurityQuestion.builder()
                .user(admin)
                .question("What is your pet's name?")
                .answer("max")
                .build());
        admin.setSecurityQuestions(adminQuestions);

        userRepository.save(admin);
        log.info("Created admin user: admin@revpay.com");

        // Personal user
        User personal = User.builder()
                .username("john_doe")
                .email("john.doe@example.com")
                .password(passwordEncoder.encode("password123"))
                .fullName("John Doe")
                .phoneNumber("9876543210")
                .role(Role.PERSONAL)
                .enabled(true)
                .failedLoginAttempts(0)
                .transactionPin(passwordEncoder.encode("1234"))
                .build();

        List<SecurityQuestion> personalQuestions = new ArrayList<>();
        personalQuestions.add(SecurityQuestion.builder()
                .user(personal)
                .question("What city were you born in?")
                .answer("newyork")
                .build());
        personal.setSecurityQuestions(personalQuestions);

        userRepository.save(personal);
        log.info("Created personal user: john.doe@example.com");

        // Business user
        User business = User.builder()
                .username("acme_corp")
                .email("contact@acme.com")
                .password(passwordEncoder.encode("business123"))
                .fullName("Acme Corporation")
                .phoneNumber("5555555555")
                .role(Role.BUSINESS)
                .enabled(true)
                .failedLoginAttempts(0)
                .transactionPin(passwordEncoder.encode("9999"))
                .build();

        List<SecurityQuestion> businessQuestions = new ArrayList<>();
        businessQuestions.add(SecurityQuestion.builder()
                .user(business)
                .question("What is your company registration number?")
                .answer("12345")
                .build());
        business.setSecurityQuestions(businessQuestions);

        userRepository.save(business);
        log.info("Created business user: contact@acme.com");
    }
}
