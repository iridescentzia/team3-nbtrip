package org.scoula.notification.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.scoula.notification.dto.NotificationDTO;
import org.scoula.notification.service.FCMService;
import org.scoula.notification.service.NotificationService;
import org.scoula.security.accounting.domain.CustomUser;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@Log4j2
@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;
    private final FCMService fcmService;

    // 로그인 사용자 알림 조회
    @GetMapping
    public ResponseEntity<List<NotificationDTO>> getNotifications(
            @AuthenticationPrincipal CustomUser customUser,
            @RequestParam(required = false) String category) {

        Integer userId = customUser.getUserId();

        List<NotificationDTO> notifications =
                (category != null && !category.isEmpty())
                        ? notificationService.getNotificationsByCategory(userId, category)
                        : notificationService.getNotificationsByUserId(userId);

        return ResponseEntity.ok(notifications);
    }

    // 알림 생성
    @PostMapping
    public ResponseEntity<Void> createNotification(
            @AuthenticationPrincipal CustomUser customUser,
            @RequestBody NotificationDTO dto) {

        Integer userId = customUser.getUserId();
        dto.setUserId(userId); // 로그인 유저 ID 주입
        log.info("Create Notification: {}", dto);

        notificationService.createNotification(dto);
        return ResponseEntity.ok().build();
    }

    // 알림 읽음 처리
    @PutMapping("/{notificationId}/read")
    public ResponseEntity<Void> readNotification(@PathVariable Integer notificationId) {
        notificationService.readNotification(notificationId);
        return ResponseEntity.ok().build();
    }


}