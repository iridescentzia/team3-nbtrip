package org.scoula.notification.config;

import org.quartz.*;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.impl.StdSchedulerFactory;
import org.quartz.spi.JobFactory;
import org.quartz.spi.TriggerFiredBundle;
import org.scoula.notification.scheduler.ReminderJob;
import org.springframework.context.ApplicationContext;
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
    public Trigger reminderTrigger(JobDetail reminderJobDetail) {
        CronScheduleBuilder scheduleBuilder = CronScheduleBuilder
                .dailyAtHourAndMinute(18, 17) //20시 00분
                .inTimeZone(java.util.TimeZone.getTimeZone("Asia/Seoul"))
                .withMisfireHandlingInstructionDoNothing(); // 미스파이어 시 건너뛰기

        return TriggerBuilder.newTrigger()
                .forJob(reminderJobDetail)
                .withIdentity("reminderTrigger")
                .withSchedule(scheduleBuilder)
                .build();
    }

    @Bean(destroyMethod = "shutdown")
    public Scheduler scheduler(
            ApplicationContext ctx,
            JobDetail reminderJobDetail,
            Trigger reminderTrigger
    ) throws SchedulerException {
        System.out.println("[Quartz] >>> scheduler() INIT HIT");
        Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();

        // 스프링 빈 주입되는 JobFactory
        scheduler.setJobFactory(new JobFactory() {
            @Override
            public Job newJob(TriggerFiredBundle bundle, Scheduler sched) throws SchedulerException {
                try {
                    Class<? extends Job> jobClass = bundle.getJobDetail().getJobClass();
                    return (Job) ctx.getAutowireCapableBeanFactory().createBean(jobClass);
                } catch (Exception e) {
                    throw new SchedulerException("Failed to create job instance", e);
                }
            }
        });

        if (!scheduler.checkExists(reminderJobDetail.getKey())) {
            scheduler.scheduleJob(reminderJobDetail, reminderTrigger);
        } else {
            scheduler.addJob(reminderJobDetail, true);
            scheduler.rescheduleJob(reminderTrigger.getKey(), reminderTrigger);
        }

        scheduler.start();

        return scheduler;
    }
}
