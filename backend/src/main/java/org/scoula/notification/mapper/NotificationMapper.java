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

    // 알림 생성
    int createNotification(NotificationVO vo);
    int createCompletedNotification(NotificationVO vo);
    void createGroupEventNotification(NotificationVO vo);

    // 알림 읽음 처리
    int readNotification(@Param("notificationId") Integer notificationId);

}
