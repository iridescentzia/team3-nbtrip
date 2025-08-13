package org.scoula.report.service;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// 기존: 실패 시 "AI 분석을 불러오는 데 실패했습니다." 반환
// 변경: 실패 시 null 반환
@Component
@Log4j2
public class ChatGPTClient {

    @Value("${openai.api.key}")
    private String openAiApiKey;

    private static final String OPENAI_URL = "https://api.openai.com/v1/chat/completions";

    public String getCompletion(String prompt) {
        RestTemplate restTemplate = new RestTemplate();

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", "gpt-4o"); // 또는 gpt-5o
        requestBody.put("temperature", 0.7);

        List<Map<String, String>> messages = new ArrayList<>();
        messages.add(Map.of("role", "system", "content",
                "너는 여행 소비 분석 전문가다. ... 한국어로 작성해."));
        messages.add(Map.of("role", "user", "content", prompt));
        requestBody.put("messages", messages);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(openAiApiKey);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

        try {
            ResponseEntity<Map> response = restTemplate.exchange(
                    OPENAI_URL, HttpMethod.POST, entity, Map.class);

            Map<String, Object> data = response.getBody();
            if (data == null) return null;

            List<Map<String, Object>> choices = (List<Map<String, Object>>) data.get("choices");
            if (choices == null || choices.isEmpty()) return null;

            Map<String, Object> message = (Map<String, Object>) choices.get(0).get("message");
            if (message == null) return null;

            String content = (String) message.get("content");
            return (content != null && !content.isBlank()) ? content : null;

        } catch (Exception e) {
            log.error("OpenAI API 호출 실패", e);
            return null; // ❗ 실패 시 null
        }
    }
}
