package org.scoula.notification.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.scoula.notification.dto.NotificationDTO;
import org.scoula.notification.service.NotificationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Log4j2
@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    //특정 유저 알림 조회
    @GetMapping("/{userId}")
    public ResponseEntity<List<NotificationDTO>> getNotifications(
            @PathVariable Integer userId,
            @RequestParam(required = false) String category){

        List<NotificationDTO> notifications = (category != null && !category.isEmpty())
                ? notificationService.getNotificationsByCategory(userId, category)
                : notificationService.getNotificationsByUserId(userId);
        return ResponseEntity.ok(notifications);
    }

    //알림 생성
    @PostMapping
    public ResponseEntity<Void> createNotification(@RequestBody NotificationDTO dto) {
        log.info("Create Notification: {}", dto);
        notificationService.createNotification(dto);
        return ResponseEntity.ok().build();
    }

    // JWT 인증된 유저의 알림 조회 (userId 파라미터 없이)
//    @GetMapping
//    public ResponseEntity<List<NotificationDTO>> getNotifications(
//            @AuthenticationPrincipal UserPrincipal principal, // JWT 인증 유저 객체
//            @RequestParam(required = false) String category) {
//
//        Integer userId = principal.getUserId(); // 토큰에서 userId 추출
//
//        log.info("userId = " + userId + ", category = " + category);
//
//        List<NotificationDTO> notifications = (category != null && !category.isEmpty())
//                ? notificationService.getNotificationsByCategory(userId, category)
//                : notificationService.getNotificationsByUserId(userId);
//        return ResponseEntity.ok(notifications);
//    }
//
//    @PostMapping
//    public ResponseEntity<Void> createNotification(
//            @RequestBody NotificationDTO dto,
//            @AuthenticationPrincipal UserPrincipal principal) {
//
//        dto.setUserId(principal.getUserId()); // 토큰에서 userId 셋팅
//        log.info("Create Notification: {}", dto);
//        notificationService.createNotification(dto);
//        return ResponseEntity.ok().build();
//    }

}
