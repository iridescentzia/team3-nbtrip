package org.scoula.report.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.scoula.report.dto.ChartDTO;
import org.scoula.report.service.ChartService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Log4j2
@RestController
@RequestMapping("/api/chart")
@RequiredArgsConstructor
public class ChartController {

    private final ChartService chartService;

    /**
     * GET /api/chart?tripId=1
     * - trip_status 체크 후
     *   • CLOSED → donutData, lineData 반환
     *   • 그 외   → 준비 중 메시지 반환
     */
    @GetMapping
    public ResponseEntity<?> getChart(@RequestParam Long tripId) {
        log.info("GET /api/chart?tripId={}", tripId);

        // 1) 상태 조회
        String status = chartService.getTripStatus(tripId);

        // 2) CLOSED면 데이터 조회
        if ("CLOSED".equals(status)) {
            List<ChartDTO> donut = chartService.getDonutChart(tripId);
            List<ChartDTO> line  = chartService.getLineChart(tripId);
            Map<String,Object> body = new HashMap<>();
            body.put("tripId",    tripId);
            body.put("status",    status);
            body.put("donutData", donut);
            body.put("lineData",  line);
            return ResponseEntity.ok(body);
        }

        // 3) 준비 중
        Map<String,Object> body = new HashMap<>();
        body.put("tripId",  tripId);
        body.put("status",  status);
        body.put("message", "차트 준비 중입니다.");
        return ResponseEntity.ok(body);
    }
}