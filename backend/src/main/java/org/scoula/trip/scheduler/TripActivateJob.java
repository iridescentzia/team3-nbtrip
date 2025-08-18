package org.scoula.trip.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.scoula.trip.service.TripService;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Log4j2
@Component
@RequiredArgsConstructor
@DisallowConcurrentExecution
public class TripActivateJob implements Job {
    private final TripService tripService;

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        log.info("여행 상태 업데이트 실행");
        int res = tripService.activateTrip(LocalDate.now());
        log.info("여행 상태 "+res+"건 업데이트 완료");
    }
}
