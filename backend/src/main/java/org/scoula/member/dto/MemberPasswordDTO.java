package org.scoula.member.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Getter
@Setter
@NoArgsConstructor
public class MemberPasswordDTO {
    // 현재 비밀번호
    @NotBlank(message = "현재 비밀번호를 입력해주세요.")
    private String currentPassword;

    // 새 비밀번호
    @NotBlank(message = "새 비밀번호를 입력해주세요.")
    @Size(min = 8, max = 20, message = "비밀번호는 8-20자 사이여야 합니다.")
    @Pattern(regexp = "^(?=.*[a-zA-Z])(?=.*[0-9])(?=.*[!@#$%^&*])", message = "비밀번호는 영문, 숫자, 특수문자를 포함해야 합니다")
    private String newPassword;

    // 새 비밀번호 확인
    @NotBlank(message = "새 비밀번호 확인을 입력해주세요.")
    private String newPasswordConfirm;
}
