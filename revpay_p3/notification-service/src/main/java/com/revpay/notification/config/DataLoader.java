package com.revpay.notification.config;

import com.revpay.notification.entity.Notification;
import com.revpay.notification.entity.NotificationCategory;
import com.revpay.notification.repository.NotificationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;

@Component
public class DataLoader implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataLoader.class);

    private final NotificationRepository notificationRepository;

    public DataLoader(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        if (notificationRepository.count() == 0) {
            log.info("Loading sample notification data...");
            loadSampleData();
        }
    }

    private void loadSampleData() {
        // Sample notifications
        Notification notification1 = Notification.builder()
                .userId(1L)
                .category(NotificationCategory.TRANSACTIONS)
                .type("PAYMENT_RECEIVED")
                .title("Payment Received")
                .message("You received $50.00 from John Doe")
                .amount(new BigDecimal("50.00"))
                .counterparty("John Doe")
                .eventStatus("COMPLETED")
                .navigationTarget("/transactions/123")
                .eventTime(LocalDateTime.now())
                .isRead(false)
                .build();

        Notification notification2 = Notification.builder()
                .userId(1L)
                .category(NotificationCategory.REQUESTS)
                .type("MONEY_REQUEST")
                .title("Money Request")
                .message("Jane Smith requested $25.00 from you")
                .amount(new BigDecimal("25.00"))
                .counterparty("Jane Smith")
                .eventStatus("PENDING")
                .navigationTarget("/requests/456")
                .eventTime(LocalDateTime.now())
                .isRead(false)
                .build();

        Notification notification3 = Notification.builder()
                .userId(1L)
                .category(NotificationCategory.ALERTS)
                .type("LOW_BALANCE")
                .title("Low Balance Alert")
                .message("Your account balance is below $100.00")
                .eventStatus("INFO")
                .navigationTarget("/wallet")
                .eventTime(LocalDateTime.now())
                .isRead(false)
                .build();

        notificationRepository.saveAll(Arrays.asList(notification1, notification2, notification3));
        log.info("Sample notification data loaded successfully");
    }
}
