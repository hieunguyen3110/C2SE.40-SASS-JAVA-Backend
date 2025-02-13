package com.capstone1.sasscapstone1.service.NotificationService;

import com.capstone1.sasscapstone1.entity.Notification;
import com.capstone1.sasscapstone1.entity.Account;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface NotificationService {
    ResponseEntity<?> getNotificationsForUser(Account account, int pageNum, int pageSize);
    ResponseEntity<?> countNotificationOfUser(Account account);
    ResponseEntity<?> getUnreadNotificationsForUser(Account account);
    ResponseEntity<?> getNotificationsSaved(Account account, int pageNum, int pageSize);
    ResponseEntity<?> getNotificationsDeleted(Account account, int pageNum, int pageSize);
    ResponseEntity<?> moveNotificationToTrash( List<Long> notificationIds);
    ResponseEntity<?> moveNotificationToSaved( List<Long> notificationIds);
    ResponseEntity<Notification> createNotification(Account user, String message, String type);
    ResponseEntity<String> markNotificationAsRead(List<Long> notificationIds);
    ResponseEntity<String> deleteNotification(List<Long> notificationIds);
}
