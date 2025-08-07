package org.scoula.mypage.dto;

import lombok.AllArgsConstructor;
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

// PUT /api/users/{userId}
public class UserUpdateRequestDTO {
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

    @Size(min = 9, message = "비밀번호는 최소 9자 이상이어야 합니다.")
    @Pattern(
            regexp = "^(?=.*[a-z])(?=.*\\d)(?=.*[!@#$%^&*])[A-Za-z\\d!@#$%^&*]{9,}$",
            message = "비밀번호는 영문 소문자, 숫자, 특수문자를 포함해 9자 이상이어야 합니다."
    )
    private String password;

    private LocalDateTime updatedAt;
}
