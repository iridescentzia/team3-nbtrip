package org.scoula.notification.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.scoula.notification.domain.NotificationVO;

import java.util.List;

@Mapper
public interface NotificationMapper {
    // 특정 유저의 모든 알림
    List<NotificationVO> findByUserId(Integer userId);
    // 특정 유저의 특정 타입 알림
    // 결제 탭(TRANSACTION만)
    List<NotificationVO> findByUserIdAndType(
            @Param("userId")Integer userId,
            @Param("notificationType")String notificationType
    );
    // 특정 유저의 여러 타입 알림 (UI 카테고리)
    // 정산 탭(SETTLEMENT, REMINDER, COMPLETED)
    List<NotificationVO> findByUserIdAndTypes(
            @Param("userId") Integer userId,
            @Param("types") List<String> types
    );
    // 알림 생성
    int insertNotification(NotificationVO vo);

    //알림 읽음 처리
    int readNotification(@Param("notificationId") Integer notificationId);

}
