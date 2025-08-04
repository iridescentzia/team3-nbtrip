package org.scoula.report.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.scoula.report.service.OpenAiService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@Log4j2
@RestController
@RequestMapping("/api/openai")
@RequiredArgsConstructor
public class AiReportController {

    private final OpenAiService OpenAiService;


    @GetMapping("/{tripId}")
    public ResponseEntity<Map<String, Object>> getReport(@PathVariable int tripId) {

        Map<String, Object> response = new HashMap<>();
        response.put("donutData", OpenAiService.getDonutReport(tripId));
        response.put("lineData", OpenAiService.getLineReport(tripId));
        return ResponseEntity.ok(response);
    }

}
