package org.scoula.security.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.scoula.security.accounting.domain.CustomUser;
import org.scoula.security.accounting.dto.AuthResultDTO;
import org.scoula.security.accounting.dto.UserInfoDTO;
import org.scoula.security.util.JwtProcessor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
@RequiredArgsConstructor
@Log4j2
public class LoginSuccessHandler implements AuthenticationSuccessHandler {
    private final JwtProcessor jwtProcessor;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {

        // 인증 결과
        CustomUser customUser = (CustomUser) authentication.getPrincipal();
        log.info("로그인 성공 - 이메일 : {}, 사용자 : {} (ID : {})", customUser.getUsername(), customUser.getNickname(), customUser.getUserId());

        // JWT 엑세스 토큰 생성(24시간 유효)
        String accessToken = jwtProcessor.generateAccessToken(
                customUser.getUsername(),  // email
                customUser.getUserId(),
                customUser.getNickname()
        );

        // 24시간 이후 만료 시간
        long tokenExpiry = System.currentTimeMillis() + (24*60*60*1000);

        // 사용자 정보 DTO
        UserInfoDTO userInfo = UserInfoDTO.builder()
                .userId(customUser.getUserId())
                .email(customUser.getUsername())
                .nickname(customUser.getNickname())
                .name(customUser.getName())
                .phoneNumber(customUser.getPhoneNumber())
                .build();

        // 성공 응답
        AuthResultDTO successResult = AuthResultDTO.success(
                accessToken, tokenExpiry, userInfo
        );

        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(HttpServletResponse.SC_OK);
        response.getWriter().write(objectMapper.writeValueAsString(successResult));
        log.info("로그인 응답 전송 완료 - 24시간 엑세스 토큰 발급");
    }
}
