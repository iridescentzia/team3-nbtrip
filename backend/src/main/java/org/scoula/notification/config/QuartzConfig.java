package org.scoula.notification.config;

import org.quartz.*;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.scoula.notification.scheduler.ReminderJob;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class QuartzConfig {
    @Bean
    public JobDetail reminderJobDetail() {
        return JobBuilder.newJob(ReminderJob.class)
                .withIdentity("reminderJob")
                .storeDurably()
                .build();
    }

    @Bean
    public Trigger reminderTrigger() {
        CronScheduleBuilder scheduleBuilder = CronScheduleBuilder
                .dailyAtHourAndMinute(17, 22) //20시 00분
                .inTimeZone(java.util.TimeZone.getTimeZone("Asia/Seoul"))
                .withMisfireHandlingInstructionDoNothing(); // 미스파이어 시 건너뛰기
        return TriggerBuilder.newTrigger()
                .forJob(reminderJobDetail())
                .withIdentity("reminderTrigger")
                .withSchedule(scheduleBuilder)
                .build();
    }
}
