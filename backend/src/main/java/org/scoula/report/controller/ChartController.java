package org.scoula.report.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.scoula.merchant.dto.MerchantDTO;
import org.scoula.report.dto.ChartDTO;
import org.scoula.report.service.ChartService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
        log.info("api 불러오는중");
        return ResponseEntity.ok(response);
    }

}
