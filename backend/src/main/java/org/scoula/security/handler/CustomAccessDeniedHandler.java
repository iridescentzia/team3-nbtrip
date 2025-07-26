package org.scoula.security.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Component
@Log4j2
public class CustomAccessDeniedHandler implements AccessDeniedHandler {
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void handle(HttpServletRequest request,
                       HttpServletResponse response,
                       AccessDeniedException accessDeniedException) throws IOException, ServletException {
        String requestURI = request.getRequestURI();
        String method = request.getMethod();
        log.warn("접근 거부 - URI : {} {}, 사유 : {}", method, requestURI, accessDeniedException.getMessage());

        // 접근 거부 응답 생성
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("success", false);
        errorResponse.put("message", getAccessDeniedMessage(requestURI));
        errorResponse.put("status", HttpServletResponse.SC_FORBIDDEN);
        errorResponse.put("timestamp", System.currentTimeMillis());
        errorResponse.put("path", requestURI);

        // JSON 응답 전송
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
    }

    // 요청 경로별 접근 거부 메시지 생성
    private String getAccessDeniedMessage(String requestURI) {
        if (requestURI.contains("/api/groups/")) {
            if (requestURI.contains("/admin")) {
                return "그룹 관리자 권한이 필요합니다.";
            } else if (requestURI.contains("/expenses")) {
                return "해당 그룹의 가계부 접근 권한이 없습니다.";
            } else {
                return "해당 여행 그룹에 참여하지 않았습니다.";
            }
        } else if (requestURI.contains("/api/payments/")) {
            return "결제 권한이 없습니다. 그룹 참여 후 이용해주세요.";
        } else if (requestURI.contains("/api/settlements/")) {
            return "정산 권한이 없습니다. 그룹장 또는 참여자만 가능합니다.";
        } else if (requestURI.contains("/api/users/")) {
            return "다른 사용자의 정보에 접근할 수 없습니다.";
        } else {
            return "해당 리소스에 접근할 권한이 없습니다.";
        }
    }
}
