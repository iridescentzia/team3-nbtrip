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

@Log4j2
@Component
public class ChatGPTClient {

    @Value("${openai.api.key}")
    private String openAiApiKey;

    private static final String OPENAI_URL = "https://api.openai.com/v1/chat/completions";

    public String getCompletion(String prompt) {
        RestTemplate restTemplate = new RestTemplate();

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", "gpt-4o");
        requestBody.put("temperature", 0.7);

        List<Map<String, String>> messages = new ArrayList<>();
        messages.add(Map.of("role", "system", "content", "너는 여행 소비 분석을 도와주는 전문가야. 사용자의 소비 지출 데이터를 비율로 분석하고, 이번 여행에 대해 개선 조언이나 피드백을 전문가처럼 해줘. 너무 딱딱하지 않고, 부드러운 조언을 포함한 한국어로 작성해."));
        messages.add(Map.of("role", "user", "content", prompt));

        requestBody.put("messages", messages);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(openAiApiKey);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

        try {
            ResponseEntity<Map> response = restTemplate.exchange(
                    OPENAI_URL,
                    HttpMethod.POST,
                    entity,
                    Map.class
            );

            Map<String, Object> data = response.getBody();
            List<Map<String, Object>> choices = (List<Map<String, Object>>) data.get("choices");
            Map<String, Object> message = (Map<String, Object>) choices.get(0).get("message");
            return (String) message.get("content");

        } catch (Exception e) {
            log.error("OpenAI API 호출 실패", e);
            return "AI 분석을 불러오는 데 실패했습니다.";
        }
    }
}
