package org.scoula.report.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.scoula.report.dto.ChartDTO;
import org.scoula.report.service.ChartService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    @GetMapping("/{tripId}/donut")
    public ResponseEntity<List<ChartDTO>> getDonutChart(@PathVariable String tripId) {
        log.info("GET /api/chart/" +tripId+"/donut");

        List<ChartDTO> charts = chartService.getDonutChart(tripId);

        return ResponseEntity.ok(charts);
    }

    @GetMapping("/{tripId}/line")
    public ResponseEntity<List<ChartDTO>> getLineChart(@PathVariable String tripId) {
        log.info("GET /api/chart/"+tripId+"/line");

        List<ChartDTO> charts = chartService.getDonutChart(tripId);

        return ResponseEntity.ok(charts);
    }
}
