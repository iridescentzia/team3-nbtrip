package org.scoula.report.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.scoula.report.service.ChartService;
import org.scoula.security.accounting.domain.CustomUser;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Log4j2
@RestController
@RequestMapping("/api/report")
@RequiredArgsConstructor
public class ChartController {

    private final ChartService chartService;

    @GetMapping("/{tripId}")
    public ResponseEntity<Map<String, Object>> getChart(@PathVariable int tripId) {
        Map<String, Object> response = new HashMap<>();
        response.put("donutData", chartService.getDonutChart(tripId));
        response.put("lineData", chartService.getLineChart(tripId));
        response.put("tripData", chartService.getTripInfo(tripId));
        return ResponseEntity.ok(response);
    }


    @GetMapping("/{tripId}/is-member")
    public ResponseEntity<Boolean> checkIsTripMember(@PathVariable int tripId,
                                                     @AuthenticationPrincipal CustomUser customUser) {
        if (customUser== null) {
            log.warn("비인증 사용자 접근: tripId={}", tripId);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(false);
        }

        log.info("사용자 {} 가 trip {} 의 멤버인지 확인 중", customUser.getUserId(), tripId);


        Integer userId = customUser.getUserId();
        boolean isMember = chartService.isTripMember(tripId, userId);



        return ResponseEntity.ok(isMember);


    }

}

