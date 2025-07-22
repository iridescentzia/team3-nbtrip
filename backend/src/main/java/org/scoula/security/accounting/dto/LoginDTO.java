package org.scoula.security.accounting.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.aspectj.bridge.IMessage;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
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
}
