package org.scoula.report.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.scoula.report.service.OpenAiService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Log4j2
@RestController
@RequestMapping("/api/openai")
@RequiredArgsConstructor
public class AiReportController {

    private final OpenAiService openAiService;

    @GetMapping("/{tripId}")
    public ResponseEntity<Map<String, Object>> getDonutReport(@PathVariable int tripId) {
        String comment = openAiService.getDonutReport(tripId);
        Map<String, Object> result = new HashMap<>();
        result.put("comment", comment);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/{tripId}/date")
    public ResponseEntity<Map<String, Object>> getLineReport(@PathVariable int tripId) {
        String comment = openAiService.getLineReport(tripId);
        Map<String, Object> result = new HashMap<>();
        result.put("comment", comment);
        return ResponseEntity.ok(result);
    }
}


