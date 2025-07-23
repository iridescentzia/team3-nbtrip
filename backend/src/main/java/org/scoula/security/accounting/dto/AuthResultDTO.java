package org.scoula.security.accounting.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AuthResultDTO {
    private boolean success;
    private String message;
    private String accessToken;
    private Long tokenExpiry;  // 토큰 만료 시간(timestamp)
    private String tokenType;
    private UserInfoDTO userInfo;

    // 로그인 성공 응답
    public static AuthResultDTO success(String accessToken, Long tokenExpiry, UserInfoDTO userInfo) {
        return AuthResultDTO.builder()
                .success(true)
                .message("로그인에 성공했습니다.")
                .accessToken(accessToken)
                .tokenExpiry(tokenExpiry)
                .tokenType("Bearer")
                .userInfo(userInfo)
                .build();
    }

    // 로그인 실패 응답
    public static AuthResultDTO failure(String message) {
        return AuthResultDTO.builder()
                .success(false)
                .message(message)
                .tokenType("Bearer")
                .build();
    }

    // 이메일/비밀번호 불일치 실패 응답
    public static AuthResultDTO invalidCredentials() {
        return failure("이메일 또는 비밀번호가 올바르지 않습니다.");
    }

    // 토큰 만료 실패 응답
    public static AuthResultDTO tokenExpired() {
        return failure("토큰이 만료되었습니다. 다시 로그인해주세요.");
    }

    // 서버 에러 응답
    public static AuthResultDTO serverError() {
        return failure("서버 오류가 발생했습니다. 잠시 후 다시 시도해주세요.");
    }
}
