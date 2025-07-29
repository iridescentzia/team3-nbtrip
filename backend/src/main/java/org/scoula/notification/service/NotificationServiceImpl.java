package org.scoula.notification.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;;
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
        return mapper.findByUserId(userId)
                .stream()
                .map(NotificationDTO::of)
                .toList();
    }

    @Override
    public List<NotificationDTO> getNotificationsByUserIdAndType(Integer userId, String type) {
        return mapper.findByUserIdAndType(userId, type)
                .stream()
                .map(NotificationDTO::of)
                .toList();
    }
    @Override
    public List<NotificationDTO> getNotificationsByCategory(Integer userId, String category) {
        List<String> types = switch (category.toUpperCase()) {
            case "TRANSACTION" -> List.of("TRANSACTION"); // 결제
            case "SETTLEMENT" -> List.of("SETTLEMENT", "REMINDER", "COMPLETED"); // 정산
            case "INVITE" -> List.of("INVITE"); // 그룹
            default -> Arrays.asList("INVITE", "TRANSACTION", "SETTLEMENT", "REMINDER", "COMPLETED"); // 전체
        };

        return mapper.findByUserIdAndTypes(userId, types)
                .stream()
                .map(NotificationDTO::of)
                .toList();
    }


    @Override
    public void createNotification(NotificationDTO dto) {
        log.info("Create Notification: {}", dto);
        mapper.insertNotification(dto.toVO());
    }
}
