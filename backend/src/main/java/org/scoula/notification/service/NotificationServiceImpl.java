package org.scoula.notification.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.scoula.notification.domain.NotificationVO;
import org.scoula.notification.dto.NotificationDTO;
import org.scoula.notification.mapper.NotificationMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Log4j2
public class NotificationServiceImpl implements NotificationService {

    private final NotificationMapper mapper;
    private final FCMService fcmService;


    // 유저 ID로 전체 알림 조회
    @Override
    public List<NotificationDTO> getNotificationsByUserId(Integer userId) {
        return mapper.findAllByUserId(userId)
                .stream()
                .map(NotificationDTO::of)
                .toList();
    }

    // 거래 알림만 조회
    @Override
    public List<NotificationDTO> getTransactionNotifications(Integer userId) {
        return mapper.findTransactionNotifications(userId)
                .stream()
                .map(NotificationDTO::of)
                .toList();
    }

    // 정산 알림만 조회
    @Override
    public List<NotificationDTO> getSettlementNotifications(Integer userId) {
        return mapper.findSettlementNotifications(userId)
                .stream()
                .map(NotificationDTO::of)
                .toList();
    }

    // 그룹 초대/참여/탈퇴 관련 알림 조회
    @Override
    public List<NotificationDTO> getGroupNotifications(Integer userId) {
        return mapper.findGroupNotifications(userId)
                .stream()
                .map(NotificationDTO::of)
                .toList();
    }

    // 카테고리에 따라 알림 조회
    @Override
    public List<NotificationDTO> getNotificationsByCategory(Integer userId, String category) {
        return switch (category.toUpperCase()) {
            case "TRANSACTION" -> getTransactionNotifications(userId);
            case "SETTLEMENT" -> getSettlementNotifications(userId);
            case "INVITE" -> getGroupNotifications(userId);
            case "GROUP_EVENT" -> getGroupNotifications(userId);
            default -> getNotificationsByUserId(userId);
        };
    }

    // 그룹 참여/탈퇴 알림 생성 (joined, left)
    @Override
    public void createGroupEventNotification(Integer fromUserId, Integer tripId, String type) {
        NotificationVO vo = NotificationVO.builder()
                .fromUserId(fromUserId)
                .tripId(tripId)
                .notificationType(type) // JOINED or LEFT
                .build();
        mapper.createGroupEventNotification(vo);
    }

    // 알림 생성 및 fcm 푸시 전송
    @Override
    public void createNotification(NotificationDTO dto) {
        String type = dto.getNotificationType().toUpperCase();

        // 결제 (TRANSACTION) 알림 -> trip 멤버 전체 insert + 푸시 알림
        if (type.equals("TRANSACTION")) {
            // 결제 생성인지 수정인지 구분해서 actionType 세팅
            if (dto.getActionType() == null || dto.getActionType().isBlank()) {
                dto.setActionType("CREATE"); // 기본값 생성
            }
            // 한글 메시지용 actionKor 세팅
            String actionKor = dto.getActionType().equalsIgnoreCase("UPDATE") ? "수정" : "등록";

            // DB insert (여행 멤버 모두에게)
            mapper.createTransactionNotificationForAll(dto.toVO());

            // 푸시 전송
            List<Integer> memberIds = mapper.findUserIdsByTripId(dto.getTripId());
            for (Integer userId : memberIds) {
                String fcmToken = mapper.findFcmTokenByUserId(userId);
                if (fcmToken != null && !fcmToken.isBlank()) {
                    try {
                        fcmService.sendPushNotification(
                                fcmToken,
                                "결제 알림이 도착했습니다.",
                                dto.getFromUserNickname() + "님이 '" + dto.getMerchantName() + "' 결제를 " + actionKor + "했습니다."
                        );
                    } catch (Exception e) {
                        log.error("TRANSACTION 푸시 실패: userId={}, tripId={}", userId, dto.getTripId(), e);

                    }
                }

            }
        }
        // 정산 요청 알림 trip 멤버 전원에게 알림 insert + 푸시 전송
        if (type.equals("SETTLEMENT")) {
            mapper.createSettlementNotificationForAll(dto.toVO());
            List<Integer> memberIds = mapper.findUserIdsByTripId(dto.getTripId());
            for (Integer userId : memberIds) {
                String fcmToken = mapper.findFcmTokenByUserId(userId);
                if (fcmToken != null && !fcmToken.isBlank()) {
                    try {
                        fcmService.sendPushNotification(
                                fcmToken,
                                "정산 요청이 도착했어요",
                                "여행 정산을 확인해 주세요"
                        );
                    } catch (Exception e) {
                        log.error("SETTLEMENT 푸시 실패: userId={}, tripId={}", userId, dto.getTripId(), e);

                    }
                }

            }
        }
        // 정산 요청 알림 trip 멤버 전원에게 알림 insert + 푸시 전송
        if (type.equals("SETTLEMENT")) {
            mapper.createSettlementNotificationForAll(dto.toVO());
            List<Integer> memberIds = mapper.findUserIdsByTripId(dto.getTripId());
            for (Integer userId : memberIds) {
                String fcmToken = mapper.findFcmTokenByUserId(userId);
                if (fcmToken != null && !fcmToken.isBlank()) {
                    try {
                        fcmService.sendPushNotification(
                                fcmToken,
                                "정산 요청이 도착했어요",
                                "여행 정산을 확인해 주세요"
                        );
                    } catch (Exception e) {
                        log.error("SETTLEMENT 푸시 실패: userId={}, tripId={}", userId, dto.getTripId(), e);
                    }
                }
            }
            return;
        }

        // 정산 완료 알림 trip 멤버 전원에게 알림 insert + 푸시 전송
        if (type.equals("COMPLETED")) {
            mapper.createCompletedNotification(dto.toVO());

            List<Integer> memberIds = mapper.findUserIdsByTripId(dto.getTripId());
            for (Integer userId : memberIds) {
                String fcmToken = mapper.findFcmTokenByUserId(userId);
                if (fcmToken != null && !fcmToken.isBlank()) {
                    try {
                        fcmService.sendPushNotification(
                                fcmToken,
                                "정산이 완료되었어요",
                                "정산이 모두 완료되었습니다."
                        );
                    } catch (Exception e) {
                        log.error("COMPLETED 푸시 실패: userId={}, tripId={}", userId, dto.getTripId(), e);
                    }
                }
            }
            return;
        }

        // 초대 알림 insert + 푸시
        if (type.equals("INVITE")) {
            String fcmToken = mapper.findFcmTokenByUserId(dto.getUserId());
            mapper.createNotification(dto.toVO());
            if (fcmToken != null && !fcmToken.isBlank()) {
                try {
                    fcmService.sendPushNotification(
                            fcmToken,
                            "여행 초대가 도착했어요",
                            "새로운 여행에 초대받았어요. 확인해보세요"
                    );
                } catch (Exception e) {
                    log.error("INVITE 푸시 실패: userId={}", dto.getUserId(), e);
                }
            }
            return;
        }
    }

    // 알림 읽음 처리
    @Override
    public void readNotification(Integer notificationId) {
        mapper.readNotification(notificationId);
    }

    // 리마인더 푸시알림
    @Override
    public void sendReminderNotifications() {
        log.info("리마인더 푸시 알림 작업 시작");

        // 1. 정산 미완료 사용자 조회
        List<Integer> userIds = mapper.findUsersNeedingReminder();

        for (Integer userId : userIds) {
            Integer tripId = mapper.findTripIdForUserPendingSettlement(userId);
            Integer fromUserId = tripId != null ? mapper.findSettlementRequester(tripId) : null;
            // 2. 알림 DB 저장
            NotificationDTO dto = NotificationDTO.builder()
                    .userId(userId)
                    .tripId(tripId)
                    .fromUserId(fromUserId)
                    .notificationType("REMINDER")
                    .build();


            log.info("리마인더 생성 대상: userId={}, tripId={}, fromUserId={}", userId, tripId, fromUserId);
            mapper.createNotification(dto.toVO());

            // 3. FCM 토큰 조회
            String fcmToken = mapper.findFcmTokenByUserId(userId);
            log.info("리마인더 전송 대상 userId={}, fcmToken={}", userId, fcmToken);
            if (fcmToken != null && !fcmToken.isBlank()) {
                try {
                    fcmService.sendPushNotification(
                            fcmToken,
                            "정산 알림이에요",
                            "정산하지 않은 내역이 있어요. 확인 부탁드립니다."
                    );
                } catch (Exception e) {
                    log.error("REMINDER 푸시 실패: userId={}", userId, e);
                }
            } else {
                log.warn("리마인더 대상 userId={} 는 FCM 토큰이 없음", userId);
            }
        }

        log.info("리마인더 푸시 알림 작업 완료");
    }

}