package org.scoula.notification.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.scoula.notification.domain.NotificationVO;

import java.util.List;

@Mapper
public interface NotificationMapper {
    // 전체 알림 (ALL)
    List<NotificationVO> findAllByUserId(@Param("userId") Integer userId);

    // 결제 알림 (TRANSACTION)
    List<NotificationVO> findTransactionNotifications(@Param("userId") Integer userId);

    // 정산 알림 (SETTLEMENT, COMPLETED)
    List<NotificationVO> findSettlementNotifications(@Param("userId") Integer userId);

    // 그룹 알림 (INVITE, JOINED, LEFT)
    List<NotificationVO> findGroupNotifications(@Param("userId") Integer userId);

    // tripId로 멤버 userId 조회 (푸시용) -> 푸시 알림 보낼 대상 조회
    List<Integer> findUserIdsByTripId(@Param("tripId") Integer tripId);

    // 정산 안한 맴버 조회
    List<Integer> findUsersNeedingReminder();
    // 리마인더 대상 trip_id 조회
    int findTripIdForUserPendingSettlement(@Param("userId") Integer userId);
    // 해당 trip의 정산 요청자 조회
    int findSettlementRequester(@Param("tripId") Integer tripId);

    // 단일 알림 생성
    int createNotification(NotificationVO vo);
    //trip 맴버 전체에게 completed 알림
    int createCompletedNotification(NotificationVO vo);
    // trip 멤버 전체(본인 제외)에게 joined/left 알림
    int createGroupEventNotification(NotificationVO vo);
    // trip 멤버 전체에게 정산 요청 알림
    void createSettlementNotificationForAll(NotificationVO vo);
    // trip 멤버 전체에게 결제 알림
    void createTransactionNotificationForAll(NotificationVO vo);
    // 'SEND' 송금 완료 시 여행 멤버 전체(본인 포함)에게 알림 전송
    void createSendNotificationForAll(NotificationVO vo);


    // 알림 읽음 처리
    int readNotification(@Param("notificationId") Integer notificationId);

    // FCM 토큰을 user_id 기준으로 조회
    String findFcmTokenByUserId(@Param("userId") Integer userId);

    // 알림 삭제
    int deleteByPaymentId(@Param("paymentId") int paymentId);

}