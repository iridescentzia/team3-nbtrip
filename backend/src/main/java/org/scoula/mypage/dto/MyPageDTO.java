package org.scoula.mypage.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MyPageDTO {
    private Integer userId;

    @Email(message = "올바른 이메일 형식을 입력해주세요.")
    @NotBlank(message = "이메일을 입력해주세요.")
    private String email;

    @NotBlank(message = "닉네임을 입력해주세요.")
    @Size(min = 2, max = 10, message = "닉네임은 2-10자 사이여야 합니다.")
    private String nickname;

    @NotBlank(message = "이름을 입력해주세요.")
    private String name;

    @Pattern(regexp = "^01[0-9]-[0-9]{4}-[0-9]{4}$", message = "올바른 휴대폰 번호 형식을 입력해주세요. (예: 010-1234-5678)")
    private String phoneNumber;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;

    private String fcmToken;

    // 비밀번호 관련 필드
    @NotBlank(message = "현재 비밀번호를 입력해주세요.")
    private String currentPassword;  // 현재 비밀번호

    @NotBlank(message = "새 비밀번호를 입력해주세요.")
    @Size(min = 8, max = 20, message = "비밀번호는 8-20자 사이여야 합니다.")
    @Pattern(regexp = "^(?=.*[a-zA-Z])(?=.*[0-9])(?=.*[!@#$%^&*])", message = "비밀번호는 영문, 숫자, 특수문자를 포함해야 합니다")
    private String newPassword;  // 새 비밀번호

    @NotBlank(message = "새 비밀번호 확인을 입력해주세요.")
    private String newPasswordConfirm;  // 새 비밀번호 확인

    // 사용자 정보 조회용 생성자(비밀번호 제외)
    public MyPageDTO(Integer userId, String email, String nickname, String name, String phoneNumber, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.userId = userId;
        this.email = email;
        this.nickname = nickname;
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // 마스킹된 휴대폰 번호 반환(중간 부분 마스킹 처리)
    public String getMaskedPhoneNumber() {
        if(phoneNumber == null || phoneNumber.length() < 13) { return "010-****-****"; }
        String[] parts = phoneNumber.split("-");
        if(parts.length == 3) { return parts[0] + "-****-" + parts[2]; }
        return "010-****-****";
    }
}
