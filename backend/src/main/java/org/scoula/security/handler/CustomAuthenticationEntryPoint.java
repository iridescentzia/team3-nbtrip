package org.scoula.security.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.security.sasl.AuthenticationException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Component
@Log4j2
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void commenct(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException) throws IOException, ServletException {
        String requestURI = request.getRequestURI();
        String method = request.getMethod();
        String clientIP = getClientIP(request);
        String authHeader = request.getHeader("Authorization");
        log.warn("미인증 접근 시도 - URI : {} {}, IP : {}, Authorization : {}",
                method, requestURI, clientIP, authHeader != null ? "Bearer ***" : "없음");

        // 인증 오류 응답 생성
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("success", false);
        errorResponse.put("message", getAuthenticationMessage(requestURI, authHeader));
        errorResponse.put("status", HttpServletResponse.SC_UNAUTHORIZED);
        errorResponse.put("timestamp", System.currentTimeMillis());
        errorResponse.put("path", requestURI);

        // JSON 응답 전송
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json=UTF-8");
        response.getWriter().write(objectMapper.writeValueAsString(errorResponse));

        private String getAuthenticationMessage(String requestURI, String authHeader) {
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {

                // 토큰이 없는 경우 (새로고침으로 인한 로그아웃)
                if (requestURI.contains("/api/groups/") ||
                        requestURI.contains("/api/payments/") ||
                        requestURI.contains("/api/settlements/")) {
                    return "로그인이 필요한 서비스입니다. 다시 로그인해주세요.";
                } else {
                    return "로그인이 필요합니다.";
                }
            } else {
                // 토큰은 있지만 유효하지 않은 경우
                return "인증 토큰이 유효하지 않습니다. 다시 로그인해주세요.";
            }
        }
    }
}
