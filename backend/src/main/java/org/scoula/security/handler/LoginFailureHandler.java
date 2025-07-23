package org.scoula.security.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.log4j.Log4j2;
import org.scoula.security.accounting.dto.AuthResultDTO;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
@Log4j2
public class LoginFailureHandler implements AuthenticationFailureHandler {
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void onAuthenticationFailure(HttpServletRequest request,
                                        HttpServletResponse response,
                                        AuthenticationException exception) throws IOException, ServletException {
        String clientIP = getClientIP(request);
        String userAgent = request.getHeader("User-Agent");
        String errorMessage;

        if(exception instanceof BadCredentialsException) {
            errorMessage = "이메일 또는 비밀번호가 올바르지 않습니다.";
            log.warn("로그인 실패 - 잘못된 자격증명");
        } else if(exception instanceof UsernameNotFoundException) {
            errorMessage = "존재하지 않는 이메일입니다.";
            log.warn("로그인 실패 - 존재하지 않는 이메일");
        } else {
            errorMessage = "로그인 중 오류가 발생했습니다. 잠시 후 다시 시도해주세요.";
            log.error("로그인 실패 - 예상치 못한 오류 : {}", exception.getMessage());
        }

        // 실패 응답 생성
        AuthResultDTO failureResult = AuthResultDTO.failure(errorMessage);

        // JSON 응답 전송
        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.getWriter().write(objectMapper.writeValueAsString(failureResult));
        log.info("로그인 실패 응답 전송 완료 - IP : {}", clientIP);
    }

    // 클라이언트 IP 주소 추출
    private String getClientIP(HttpServletRequest request) {
        String clientIP = request.getHeader("X-Forwarded-For");

        if (clientIP == null || clientIP.isEmpty() || "unknown".equalsIgnoreCase(clientIP)) {
            clientIP = request.getHeader("X-Real-IP");
        }
        if(clientIP == null || clientIP.isEmpty() || "unknown".equalsIgnoreCase(clientIP)) {
            clientIP = request.getRemoteAddr();
        }
        return clientIP;
    }
}
