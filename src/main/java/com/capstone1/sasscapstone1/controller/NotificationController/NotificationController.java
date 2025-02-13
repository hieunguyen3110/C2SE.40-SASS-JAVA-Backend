package com.capstone1.sasscapstone1.controller.NotificationController;

import com.capstone1.sasscapstone1.entity.Account;
import com.capstone1.sasscapstone1.request.NotificationRequest;
import com.capstone1.sasscapstone1.service.NotificationService.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import com.capstone1.sasscapstone1.exception.NotificationException;

@RestController
@RequestMapping("/notification")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;
    // Lấy tất cả thông báo cho người dùng hiện tại
    @GetMapping
    public ResponseEntity<?> getUserNotifications(
            @RequestParam(defaultValue = "0") int pageNum,
            @RequestParam(defaultValue = "5") int pageSize) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            Account account = (Account) authentication.getPrincipal();
            return notificationService.getNotificationsForUser(account, pageNum,pageSize);
        } else {
            throw new NotificationException("You are not authorized to perform this action.");
        }
    }
    @GetMapping("/saved")
    public ResponseEntity<?> getUserNotificationsSaved(
            @RequestParam(defaultValue = "0") int pageNum,
            @RequestParam(defaultValue = "5") int pageSize) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            Account account = (Account) authentication.getPrincipal();
            return notificationService.getNotificationsSaved(account, pageNum,pageSize);
        } else {
            throw new NotificationException("You are not authorized to perform this action.");
        }
    }

    @GetMapping("/deleted")
    public ResponseEntity<?> getUserNotificationsDeletedFlag(
            @RequestParam(defaultValue = "0") int pageNum,
            @RequestParam(defaultValue = "5") int pageSize) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            Account account = (Account) authentication.getPrincipal();
            return notificationService.getNotificationsDeleted(account, pageNum,pageSize);
        } else {
            throw new NotificationException("You are not authorized to perform this action.");
        }
    }

    @PostMapping("/move-to-saved")
    public ResponseEntity<?> moveNotifyToSaved(@RequestBody NotificationRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            return notificationService.moveNotificationToSaved(request.getNotificationIds());
        } else {
            throw new NotificationException("You are not authorized to perform this action.");
        }
    }

    @PostMapping("/move-to-trash")
    public ResponseEntity<?> moveNotifyToTrash(@RequestBody NotificationRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            return notificationService.moveNotificationToTrash(request.getNotificationIds());
        } else {
            throw new NotificationException("You are not authorized to perform this action.");
        }
    }

    @GetMapping("/count")
    public ResponseEntity<?> countNotification(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            Account account = (Account) authentication.getPrincipal();
            return notificationService.countNotificationOfUser(account);
        } else {
            throw new NotificationException("You are not authorized to perform this action.");
        }
    }

    // Đánh dấu thông báo đã đọc
    @PutMapping("/read")
    public ResponseEntity<String> markAsRead(@RequestBody NotificationRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            return notificationService.markNotificationAsRead(request.getNotificationIds());
        } else {
            throw new NotificationException("You are not authorized to perform this action.");
        }
    }

    // Xóa thông báo
    @DeleteMapping("/delete")
    public ResponseEntity<String> deleteNotification(@RequestBody NotificationRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            return notificationService.deleteNotification(request.getNotificationIds());
        } else {
            throw new NotificationException("You are not authorized to perform this action.");
        }
    }
}
