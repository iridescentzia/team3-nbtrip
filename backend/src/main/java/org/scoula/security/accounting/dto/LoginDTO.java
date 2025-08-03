package org.scoula.security.accounting.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.log4j.Log4j2;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import java.io.IOException;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Log4j2
public class LoginDTO {
    @Email(message = "올바른 이메일 형식을 입력해주세요")
    @NotBlank(message = "이메일은 필수입니다")
    private String email;

    @NotBlank(message = "비밀번호는 필수입니다")
    private String password;

    // 푸시 알림용 FCM 토큰
    private String fcmToken;

    // 로그인 유지 여부
    private boolean rememberMe = false;

    // JSON 요청에서 LoginDTO 객체 생성
    public static LoginDTO of(HttpServletRequest request) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            // 유효성 수동 체크
            LoginDTO loginDTO = objectMapper.readValue(request.getInputStream(), LoginDTO.class);
            if (loginDTO.getEmail() == null || loginDTO.getEmail().trim().isEmpty()) {
                log.warn("이메일 누락");
                throw new IllegalArgumentException("이메일을 입력해주세요.");
            }
            if (loginDTO.getPassword() == null || loginDTO.getPassword().trim().isEmpty()) {
                log.warn("비밀번호 누락");
                throw new IllegalArgumentException("비밀번호를 입력해주세요.");
            }
            log.debug("로그인 DTO 파싱 완료 - 이메일: {}, FCM 토큰: {}, 로그인유지: {}",
                    loginDTO.getEmail(),
                    loginDTO.getFcmToken() != null ? "있음" : "없음",
                    loginDTO.isRememberMe());
            return loginDTO;
        } catch (IOException e) {
            log.error("JSON 파싱 실패 - 메시지: {}", e.getMessage(), e);
            throw new RuntimeException("요청 본문(JSON) 파싱 중 오류가 발생했습니다.", e);
        } catch (IllegalArgumentException e) {
            log.error("입력 값 검증 실패 - {}", e.getMessage());
            throw new RuntimeException("입력값 오류: " + e.getMessage(), e);
        } catch (Exception e) {
            log.error("예기치 못한 오류 발생 - {}", e.getMessage(), e);
            throw new RuntimeException("로그인 요청 처리 중 오류가 발생했습니다.", e);
        }
    }
}