package com.capstone1.sasscapstone1.service.NotificationService;

import com.capstone1.sasscapstone1.dto.NotificationDto.NotificationDto;
import com.capstone1.sasscapstone1.entity.Notification;
import com.capstone1.sasscapstone1.entity.Account;
import com.capstone1.sasscapstone1.exception.NotificationException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import com.capstone1.sasscapstone1.repository.Notification.NotificationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;

    private NotificationDto mapToDto(Notification notification) {
        return NotificationDto.builder()
                .notificationId(notification.getNotificationId())
                .message(notification.getMessage())
                .type(notification.getType())
                .isRead(notification.getIsRead())
                .createdAt(notification.getCreatedAt())
                .build();
    }

    @Override
    public ResponseEntity<?> getNotificationsForUser(Account account, int pageNum, int pageSize) {
        try {
            PageRequest pageable = PageRequest.of(pageNum, pageSize);
            Page<Notification> notifications = notificationRepository.findByAccountOrderByCreatedAtDesc(account.getAccountId(), pageable);
            List<NotificationDto> notificationDtos = notifications.map(this::mapToDto).stream().toList();
            return ResponseEntity.ok(notificationDtos);
        } catch (Exception e) {
            throw new NotificationException(e.getMessage());
        }
    }

    @Override
    public ResponseEntity<?> countNotificationOfUser(Account account) {
        try {
            Object[] result = notificationRepository.countNotifications(account.getAccountId());
            Object[] innerArray = (Object[]) result[0];
            Map<String, Long> counts = new HashMap<>();
            counts.put("total", ((Number) innerArray[0]).longValue());
            counts.put("saved", ((Number) innerArray[1]).longValue());
            counts.put("deleted", ((Number) innerArray[2]).longValue());
            counts.put("unRead", ((Number) innerArray[3]).longValue());
            return ResponseEntity.ok(counts);
        } catch (Exception e) {
            throw new NotificationException(e.getMessage());
        }
    }

    @Override
    public ResponseEntity<?> getUnreadNotificationsForUser(Account account) {
        try {
            List<Notification> unreadNotifications = notificationRepository.findByAccountAndIsReadFalseOrderByCreatedAtDesc(account);
            List<NotificationDto> notificationDtos = unreadNotifications.stream().map(this::mapToDto).toList();
            return ResponseEntity.ok(notificationDtos);
        } catch (Exception e) {
            throw new NotificationException(e.getMessage());
        }
    }

    @Override
    public ResponseEntity<?> getNotificationsSaved(Account account, int pageNum, int pageSize) {
        try {
            PageRequest pageable = PageRequest.of(pageNum, pageSize);
            Page<Notification> notifications = notificationRepository.findNotifySaveByAccount(account.getAccountId(), pageable);
            List<NotificationDto> notificationDtos = notifications.map(this::mapToDto).stream().toList();
            return ResponseEntity.ok(notificationDtos);
        } catch (Exception e) {
            throw new NotificationException(e.getMessage());
        }
    }

    @Override
    public ResponseEntity<?> getNotificationsDeleted(Account account, int pageNum, int pageSize) {
        try {
            PageRequest pageable = PageRequest.of(pageNum, pageSize);
            Page<Notification> notifications = notificationRepository.findNotifyDeleteFlagByAccount(account.getAccountId(), pageable);
            List<NotificationDto> notificationDtos = notifications.map(this::mapToDto).stream().toList();
            return ResponseEntity.ok(notificationDtos);
        } catch (Exception e) {
            throw new NotificationException(e.getMessage());
        }
    }

    @Override
    public ResponseEntity<?> moveNotificationToTrash(List<Long> notificationIds) {
        try {
            for (Long notificationId : notificationIds) {
                Optional<Notification> optionalNotification = notificationRepository.findById(notificationId);
                if (optionalNotification.isPresent()) {
                    Notification findNotification = optionalNotification.get();
                    findNotification.setDeletedFlag(true);
                    findNotification.setIsSaved(false);
                    notificationRepository.save(findNotification);
                } else {
                    throw new NotificationException("Notification not found with ID: " + notificationId);
                }
            }
            return ResponseEntity.ok("Chuyển vào thùng rác thành công");
        } catch (Exception e) {
            throw new NotificationException(e.getMessage());
        }
    }

    @Override
    public ResponseEntity<?> moveNotificationToSaved(List<Long> notificationIds) {
        try {
            for (Long notificationId : notificationIds) {
                Optional<Notification> optionalNotification = notificationRepository.findById(notificationId);
                if (optionalNotification.isPresent()) {
                    Notification findNotification = optionalNotification.get();
                    findNotification.setDeletedFlag(false);
                    findNotification.setIsSaved(true);
                    notificationRepository.save(findNotification);
                } else {
                    throw new NotificationException("Notification not found with ID: " + notificationId);
                }
            }
            return ResponseEntity.ok("Lưu trữ thông báo thành công");
        } catch (Exception e) {
            throw new NotificationException(e.getMessage());
        }
    }

    @Override
    public ResponseEntity<Notification> createNotification(Account account, String message, String type) {
        try {
            Notification notification = new Notification();
            notification.setAccount(account);
            notification.setMessage(message);
            notification.setType(type);
            notificationRepository.save(notification);
            return ResponseEntity.ok(notification);
        } catch (Exception e) {
            throw new NotificationException("Error creating notification: " + e.getMessage());
        }
    }

    @Override
    public ResponseEntity<String> markNotificationAsRead(List<Long> notificationIds) {
        try {
            for (Long notificationId : notificationIds) {
                Optional<Notification> optionalNotification = notificationRepository.findById(notificationId);
                if (optionalNotification.isPresent()) {
                    Notification notification = optionalNotification.get();
                    notification.setIsRead(true);
                    notificationRepository.save(notification);
                } else {
                    throw new NotificationException("Notification not found with ID: " + notificationId);
                }
            }
            return ResponseEntity.ok("All notifications have been read");
        } catch (Exception e) {
            throw new NotificationException("Error marking notification as read: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public ResponseEntity<String> deleteNotification(List<Long> notificationIds) {
        try {
            for (Long notificationId : notificationIds) {
                Optional<Notification> optionalNotification = notificationRepository.findById(notificationId);
                if (optionalNotification.isPresent()) {
                    Notification notification = optionalNotification.get();
                    notificationRepository.delete(notification);
                } else {
                    throw new NotificationException("Notification not found with ID: " + notificationId);
                }
            }
            return ResponseEntity.ok("Notification deleted successfully.");
        } catch (Exception e) {
            throw new NotificationException("Error deleting notification: " + e.getMessage());
        }
    }
}
