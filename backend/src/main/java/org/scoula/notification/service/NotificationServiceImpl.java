package org.scoula.notification.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;;
import org.scoula.notification.domain.NotificationVO;
import org.scoula.notification.dto.NotificationDTO;
import org.scoula.notification.mapper.NotificationMapper;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
@Log4j2
public class NotificationServiceImpl implements NotificationService {

    private final NotificationMapper mapper;

    @Override
    public List<NotificationDTO> getNotificationsByUserId(Integer userId) {
        return mapper.findAllByUserId(userId)
                .stream()
                .map(NotificationDTO::of)
                .toList();
    }


    @Override
    public List<NotificationDTO> getTransactionNotifications(Integer userId) {
        return mapper.findTransactionNotifications(userId)
                .stream()
                .map(NotificationDTO::of)
                .toList();
    }

    @Override
    public List<NotificationDTO> getSettlementNotifications(Integer userId) {
        return mapper.findSettlementNotifications(userId)
                .stream()
                .map(NotificationDTO::of)
                .toList();
    }

    @Override
    public List<NotificationDTO> getGroupNotifications(Integer userId) {
        return mapper.findGroupNotifications(userId)
                .stream()
                .map(NotificationDTO::of)
                .toList();
    }

    @Override
    public List<NotificationDTO> getNotificationsByCategory(Integer userId, String category) {
        return switch (category.toUpperCase()) {
            case "TRANSACTION" -> getTransactionNotifications(userId);
            case "SETTLEMENT" -> getSettlementNotifications(userId);
            case "INVITE" -> getGroupNotifications(userId);
            default -> getNotificationsByUserId(userId);
        };
    }

    @Override
    public void createGroupEventNotification(Integer fromUserId, Integer tripId, String type) {
        NotificationVO vo = NotificationVO.builder()
                .fromUserId(fromUserId)
                .tripId(tripId)
                .notificationType(type) // JOINED or LEFT
                .build();
        mapper.createGroupEventNotification(vo);
    }


    @Override
    public void createNotification(NotificationDTO dto) {
        if ("COMPLETED".equalsIgnoreCase(dto.getNotificationType())) {
            mapper.createCompletedNotification(dto.toVO());
        } else {
            mapper.createNotification(dto.toVO());
        }
    }

    @Override
    public void readNotification(Integer notificationId) {
        mapper.readNotification(notificationId);
    }
}
