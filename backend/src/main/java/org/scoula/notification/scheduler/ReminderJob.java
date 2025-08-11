package org.scoula.notification.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.scoula.notification.service.NotificationService;
import org.springframework.stereotype.Component;

@Log4j2
@Component
@RequiredArgsConstructor
@DisallowConcurrentExecution // 중복 실행 방지
public class ReminderJob implements Job {

    private final NotificationService notificationService;

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        log.info("리마인더 푸시 알림 전송 작업 시작");

        // NotificationService에서 리마인더 발송 로직 호출
        notificationService.sendReminderNotifications();

        log.info("리마인더 푸시 알림 전송 작업 완료");
    }
}
