package org.scoula.notification.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.scoula.notification.domain.NotificationVO;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class NotificationDTO {
    private Integer notificationId;
    private Integer userId;
    private Integer fromUserId;
    private Integer tripId;
    private Integer paymentId;
    private String notificationType;
    private String sendAt;
    private Boolean isRead;

    private String tripName;
    private String merchantName;
    private String fromUserNickname;
    private Integer amount;
    private String memberStatus;
    private String paymentType; // PREPAID, QR, OTHER
    private String actionType;  // CREATE, UPDATE

    public static NotificationDTO of(NotificationVO vo){
        return vo == null ? null : NotificationDTO.builder()
                .notificationId(vo.getNotificationId())
                .userId(vo.getUserId())
                .fromUserId(vo.getFromUserId())
                .tripId(vo.getTripId())
                .paymentId(vo.getPaymentId())
                .notificationType(vo.getNotificationType())
                .isRead(vo.getIsRead())
                .sendAt(vo.getSendAt() != null ? vo.getSendAt().toString() : "")
                .tripName(vo.getTripName())
                .merchantName(vo.getMerchantName())
                .fromUserNickname(vo.getFromUserNickname())
                .amount(vo.getAmount())
                .memberStatus(vo.getMemberStatus())
                .paymentType(vo.getPaymentType())
                .actionType(vo.getActionType())
                .build();
    }

    public NotificationVO toVO(){
        return NotificationVO.builder()
                .notificationId(notificationId)
                .userId(userId)
                .fromUserId(fromUserId)
                .tripId(tripId)
                .paymentId(paymentId)
                .notificationType(notificationType)
                .isRead(isRead)
                .memberStatus(memberStatus)
                // .sendAt(java.sql.Timestamp.valueOf(sendAt))
                .tripName(tripName)
                .merchantName(merchantName)
                .fromUserNickname(fromUserNickname)
                .amount(amount)
                .paymentType(paymentType)
                .actionType(actionType)
                .build();
    }
}