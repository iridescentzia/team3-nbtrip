package org.scoula.member.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Getter
@Setter
@NoArgsConstructor
public class UserUpdateDTO {
    // 수정할 nickname
    @NotBlank(message = "닉네임을 입력해주세요.")
    @Size(min = 2, max = 100, message = "닉네임은 2-100자 사이여야 합니다.")
    private String nickname;

    // 수정할 name
    @NotBlank(message = "이름을 입력해주세요.")
    @Size(min = 2, max = 30, message = "이름은 2-30자 사이여야 합니다.")
    private String name;

    // 수정할 phoneNumber
    @NotBlank(message = "휴대폰 번호를 입력해주세요.")
    @Size(max = 30, message = "휴대폰 번호는 30자 이내여야 합니다.")
    @Pattern(regexp = "^01[0-9]-[0-9]{4}-[0-9]{4}$", message = "올바른 휴대폰 번호 형식이 아닙니다.")
    private String phoneNumber;

    // 수정할 email
    @Email(message = "올바른 이메일 형식이 아닙니다.")
    @Size(max = 100, message = "이메일은 100자 이내여야 합니다.")
    private String email;
}
