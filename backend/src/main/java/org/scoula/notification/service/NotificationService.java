package org.scoula.notification.service;

import org.scoula.notification.dto.NotificationDTO;
import java.util.List;


public interface NotificationService {
    // 특정 유저의 모든 알림
    List<NotificationDTO> getNotificationsByUserId(Integer userId);
    // 특정 유저의 특정 타입 알림
    List<NotificationDTO> getNotificationsByUserIdAndType(Integer userId, String type);
    // 특정 유저의 여러 타입 알림 조회 (UI 카테고리용)
    List<NotificationDTO> getNotificationsByCategory(Integer userId, String category);
    // 알림 생성
    void createNotification(NotificationDTO dto);
}
