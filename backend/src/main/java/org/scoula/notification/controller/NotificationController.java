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


//    //푸시 알림 테스트용 엔드포인트
//    @GetMapping("/fcm/test")
//    public ResponseEntity<String> testPush() {
//        try {
//            // 발급받은 브라우저 토큰을 여기에 넣기
//            String testToken = "ePqdrZTwBt8De-kZtTX2Jg:APA91bH9S6-WC-Ayz4cvzZXNuIsVpg4voavRJzsE79k5vfQtpgaVmnvm25s1aLFV7fX878J-yHjWQzRIETQnrgr9DXSk7Zemm5X8MtToFS651rDK-H7Hdls";
//
//            fcmService.sendPushNotification(
//                    testToken,
//                    "테스트 알림",
//                    "푸시 알림이 정상 동작하는지 확인 중입니다"
//            );
//
//            return ResponseEntity.ok("푸시 발송 시도 성공");
//        } catch (Exception e) {
//            log.error("푸시 발송 실패", e);
//            return ResponseEntity.status(500).body("푸시 발송 실패: " + e.getMessage());
//        }
//    }
//
//    //특정 유저 알림 조회
//    @GetMapping("/{userId}")
//    public ResponseEntity<List<NotificationDTO>> getNotifications(
//            @PathVariable Integer userId,
//            @RequestParam(required = false) String category){
//
//        List<NotificationDTO> notifications = (category != null && !category.isEmpty())
//                ? notificationService.getNotificationsByCategory(userId, category)
//                : notificationService.getNotificationsByUserId(userId);
//        return ResponseEntity.ok(notifications);
//    }
//
//    //알림 생성
//    @PostMapping
//    public ResponseEntity<Void> createNotification(@RequestBody NotificationDTO dto) {
//        log.info("Create Notification: {}", dto);
//        notificationService.createNotification(dto);
//        return ResponseEntity.ok().build();
//    }
//
//    //알림 읽음 처리
//    @PutMapping("/{notificationId}/read")
//    public ResponseEntity<Void> readNotification(@PathVariable Integer notificationId) {
//        notificationService.readNotification(notificationId);
//        return ResponseEntity.ok().build();
//    }


    // 토큰 기반 인증

    // 푸시 알림 테스트용
    @GetMapping("/fcm/test")
    public ResponseEntity<String> testPush() {
        try {
            String testToken = "브라우저에서 발급받은 유효한 FCM 토큰";
            fcmService.sendPushNotification(
                    testToken,
                    "테스트 알림",
                    "푸시 알림이 정상 동작하는지 확인 중입니다"
            );
            return ResponseEntity.ok("푸시 발송 시도 성공");
        } catch (Exception e) {
            log.error("푸시 발송 실패", e);
            return ResponseEntity.status(500).body("푸시 발송 실패: " + e.getMessage());
        }
    }
    

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