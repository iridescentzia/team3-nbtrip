package org.scoula.security.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class JsonResponse {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static void sendError(HttpServletResponse response, HttpStatus status, String message) {
        try {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("status", status.value());
            errorResponse.put("message", message);
            errorResponse.put("success", false);
            errorResponse.put("timestamp", System.currentTimeMillis());

            response.setStatus(status.value());
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
        } catch (IOException e) {
            log.error("JSON 오류 응답 생성 실패 : {}", e.getMessage());
        }
    }
}
