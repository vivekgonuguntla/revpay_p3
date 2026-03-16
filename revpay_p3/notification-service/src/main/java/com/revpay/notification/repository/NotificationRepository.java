package com.revpay.notification.repository;

import com.revpay.notification.entity.Notification;
import com.revpay.notification.entity.NotificationCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    List<Notification> findByUserId(Long userId);

    List<Notification> findByUserIdAndCategory(Long userId, NotificationCategory category);

    List<Notification> findByUserIdAndIsReadFalse(Long userId);

    Long countByUserIdAndIsReadFalse(Long userId);

    List<Notification> findByUserIdOrderByCreatedAtDesc(Long userId);

    List<Notification> findByUserIdAndCategoryOrderByCreatedAtDesc(Long userId, NotificationCategory category);

    List<Notification> findByUserIdAndIsReadFalseOrderByCreatedAtDesc(Long userId);

    List<Notification> findByUserIdAndCategoryAndIsReadFalseOrderByCreatedAtDesc(Long userId, NotificationCategory category);
}
