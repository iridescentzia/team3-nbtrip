package org.scoula.notification.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationVO {
    // notification 테이블 컬럼
    private Integer notificationId; // 알림 ID
    private Integer userId; // 알람 받는 유저 ID
    private Integer fromUserId; // 알람 발생시킨 유저 ID
    private Integer tripId; // 여행 ID
    private Integer paymentId; // 결제 ID
    private String notificationType; // 알림 타입
    private Timestamp sendAt; // 발송 시간
    private Boolean isRead; // 알림 읽음 여부

    // join으로 가져오는 값
    private Integer amount;         // 결제 금액
    private String merchantName;    // 가맹점 이름
    private String tripName;       // 여행 이름
    private String fromUserNickname; // 알람 발생 유저 닉네임
    private String memberStatus; // 그룹 참여 상태
    private String paymentType; // PREPAID, QR, OTHER
    private String actionType;  // CREATE, UPDATE
}