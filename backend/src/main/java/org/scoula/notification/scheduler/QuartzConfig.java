package org.scoula.notification.scheduler;

import org.quartz.*;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
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
                .dailyAtHourAndMinute(20, 0); //20시 00분

        return TriggerBuilder.newTrigger()
                .forJob(reminderJobDetail())
                .withIdentity("reminderTrigger")
                .withSchedule(scheduleBuilder)
                .build();
    }
}
