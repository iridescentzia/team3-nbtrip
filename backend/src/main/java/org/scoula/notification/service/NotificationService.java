package org.scoula.notification.service;

import org.scoula.notification.dto.NotificationDTO;
import java.util.List;


public interface NotificationService {
    // 전체 알림
    List<NotificationDTO> getNotificationsByUserId(Integer userId);

    // 결제 알림
    List<NotificationDTO> getTransactionNotifications(Integer userId);

    // 정산 알림
    List<NotificationDTO> getSettlementNotifications(Integer userId);

    // 그룹 알림
    List<NotificationDTO> getGroupNotifications(Integer userId);

    // 카테고리별 (ALL, TRANSACTION, SETTLEMENT, INVITE)
    List<NotificationDTO> getNotificationsByCategory(Integer userId, String category);

    // 알림 생성
    void createNotification(NotificationDTO dto);

    void createGroupEventNotification(Integer fromUserId, Integer tripId, String type);

    // 알림 읽음 처리
    void readNotification(Integer notificationId);

}
