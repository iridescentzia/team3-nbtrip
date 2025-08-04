package org.scoula.security.handler;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.scoula.member.dto.MemberFcmTokenDTO;
import org.scoula.member.service.MemberService;
import org.scoula.security.accounting.domain.CustomUser;
import org.scoula.security.accounting.dto.AuthResultDTO;
import org.scoula.security.accounting.dto.UserInfoDTO;
import org.scoula.security.util.JwtProcessor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Log4j2
public class LoginSuccessHandler implements AuthenticationSuccessHandler {
    private final JwtProcessor jwtProcessor;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final ApplicationContext applicationContext;

    // MemberService 지연 로딩
    private MemberService getMemberService() { return applicationContext.getBean(MemberService.class); }

    /**
     * 로그인 성공 시 처리 메인 로직
     * 1. 사용자 정보 추출
     * 2. FCM 토큰 파싱 및 저장 (여행 그룹 알림용)
     * 3. JWT 액세스 토큰 생성
     * 4. 로그인 성공 응답 전송
     */
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {

        // ===== 1. 인증된 사용자 정보 추출 =====
        CustomUser customUser = (CustomUser) authentication.getPrincipal();
        Integer userId = customUser.getUserId();
        String email = customUser.getUsername();
        String nickname = customUser.getNickname();
        log.info("로그인 성공 - 이메일: {}, 사용자: {} (ID: {})", email, nickname, userId);

        // ===== 2. FCM 토큰 추출 및 저장 (여행 그룹 알림 시스템용) =====
        String fcmToken = extractFcmToken(request);
        saveFcmToken(userId, fcmToken);

        // ===== 3. JWT 액세스 토큰 생성 (24시간 유효) =====
        String accessToken = jwtProcessor.generateAccessToken(email, userId, nickname);
        long tokenExpiry = System.currentTimeMillis() + (24 * 60 * 60 * 1000);

        // ===== 4. 사용자 정보 DTO 구성 =====
        UserInfoDTO userInfo = UserInfoDTO.builder()
                .userId(userId)
                .email(email)
                .nickname(nickname)
                .name(customUser.getName())
                .phoneNumber(customUser.getPhoneNumber())
                .fcmToken(fcmToken)
                .build();

        // ===== 5. 로그인 성공 응답 전송 =====
        sendSuccessResponse(response, accessToken, tokenExpiry, userInfo);
        log.info("로그인 응답 전송 완료 - JWT 토큰 발급 완료");
    }

    /**
     * FCM 토큰 추출
     * HttpServletRequest의 InputStream 재사용 문제를 해결하기 위해
     * 파라미터와 헤더에서 FCM 토큰을 추출
     *
     * @param request HTTP 요청 객체
     * @return FCM 토큰 (없으면 null)
     */
    private String extractFcmToken(HttpServletRequest request) {
        try {
            // 1차: request parameter에서 FCM 토큰 추출
            Object attributeToken = request.getAttribute("fcmToken");
            if (attributeToken != null) {
                String fcmToken = attributeToken.toString().trim();
                if (!fcmToken.isEmpty()) {
                    log.debug("FCM 토큰 추출 성공 (attribute) - 길이: {}", fcmToken.length());
                    return fcmToken;
                }
            }

            // 2차: request header에서 FCM 토큰 추출
            String fcmToken = request.getHeader("X-FCM-Token");
            if (fcmToken != null && !fcmToken.trim().isEmpty()) {
                fcmToken = fcmToken.trim();
                if (fcmToken.length() >= 50) {
                    log.debug("FCM 토큰 추출 성공 (header) - 길이: {}", fcmToken.length());
                    return fcmToken;
                } else {
                    log.warn("헤더의 FCM 토큰 길이가 비정상적으로 짧음 - 길이: {}", fcmToken.length());
                }
            }
            log.debug("FCM 토큰을 찾을 수 없음");
            return null;
        } catch (Exception e) {
            log.warn("FCM 토큰 추출 중 오류 발생: {}", e.getMessage());
            return null;
        }
    }

    /**
     * FCM 토큰 저장
     * 여행 그룹 알림 시스템을 위한 FCM 토큰을 데이터베이스에 저장
     * - 지출 알림, 정산 요청, 그룹 초대 등에 사용
     *
     * @param userId 사용자 ID
     * @param fcmToken FCM 토큰
     */
    private void saveFcmToken(Integer userId, String fcmToken) {
        if (fcmToken != null && !fcmToken.isEmpty()) {
            try {
                MemberFcmTokenDTO dto = MemberFcmTokenDTO.builder()
                        .fcmToken(fcmToken)
                        .build();

                getMemberService().updateFcmToken(userId, dto);
                log.info("FCM 토큰 저장 성공 - userId: {}, 토큰 길이: {}", userId, fcmToken.length());

            } catch (Exception e) {
                log.error("FCM 토큰 저장 실패 - userId: {}, 오류: {}", userId, e.getMessage(), e);
                // FCM 토큰 저장 실패해도 로그인은 계속 진행
                // 여행 그룹 알림 기능만 제한됨
            }
        } else {
            log.warn("FCM 토큰이 요청에 포함되지 않음 - userId: {} (여행 그룹 알림 기능 제한)", userId);
        }
    }

    /**
     * 로그인 성공 응답 전송
     * 클라이언트에게 JWT 토큰과 사용자 정보를 JSON 형태로 전송
     *
     * @param response HTTP 응답 객체
     * @param accessToken JWT 액세스 토큰
     * @param tokenExpiry 토큰 만료 시간
     * @param userInfo 사용자 정보
     * @throws IOException JSON 응답 생성 실패 시
     */
    private void sendSuccessResponse(HttpServletResponse response,
                                     String accessToken,
                                     long tokenExpiry,
                                     UserInfoDTO userInfo) throws IOException {
        try {
            // 로그인 성공 응답 DTO 생성
            AuthResultDTO successResult = AuthResultDTO.success(accessToken, tokenExpiry, userInfo);

            // HTTP 응답 설정
            response.setContentType("application/json;charset=UTF-8");
            response.setStatus(HttpServletResponse.SC_OK);

            // CORS 헤더 설정 (필요시)
            response.setHeader("Access-Control-Allow-Origin", "*");
            response.setHeader("Access-Control-Allow-Methods", "POST, GET, PUT, DELETE, OPTIONS");
            response.setHeader("Access-Control-Allow-Headers", "Content-Type, Authorization, FCM-Token");

            // JSON 응답 전송
            String jsonResponse = objectMapper.writeValueAsString(successResult);
            response.getWriter().write(jsonResponse);

        } catch (Exception e) {
            log.error("로그인 응답 전송 실패: {}", e.getMessage(), e);

            // 응답 전송 실패 시 기본 성공 응답
            response.setStatus(HttpServletResponse.SC_OK);
            response.getWriter().write("{\"success\":true,\"message\":\"로그인 성공\"}");
        }
    }
}
