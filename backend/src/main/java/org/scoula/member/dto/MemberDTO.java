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
public class MemberDTO {
    // email
    @NotBlank(message = "이메일을 입력해주세요.")
    @Email(message = "올바른 이메일 형식이 아닙니다")
    @Size(max = 100, message = "이메일은 100자 이내여야 합니다")
    private String email;

    // password : 8-20자, 영문+숫자+특수문자 조합
    @NotBlank(message = "비밀번호를 입력해주세요.")
    @Size(min = 8, max = 20, message = "비밀번호는 8-20자 사이여야 합니다.")
    @Pattern(regexp = "^(?=.*[a-zA-Z])(?=.*[0-9])(?=.*[!@#$%^&*])", message = "비밀번호는 영문, 숫자, 특수문자를 포함해야 합니다.")
    private String password;

    // password 확인
    @NotBlank(message = "비밀번호 확인을 입력해주세요.")
    private String passwordConfirm;

    // nickname : 2-100자, 고유값
    @NotBlank(message = "닉네임을 입력해주세요.")
    @Size(min = 2, max = 100, message = "닉네임은 2-100자 사이여야 합니다.")
    private String nickname;

    // name
    @NotBlank(message = "이름을 입력해주세요.")
    @Size(min = 2, max = 30, message = "이름은 2-30자 사이여야 합니다.")
    private String name;

    // phoneNumber
    @NotBlank(message = "휴대폰 번호를 입력해주세요.")
    @Size(max = 30, message = "휴대폰 번호는 30자 이내여야 합니다.")
    @Pattern(regexp = "^01[0-9]-[0-9]{4}-[0-9]{4}$", message = "올바른 휴대폰 번호 형식이 아닙니다(010-1234-5678).")
    private String phoneNumber;

    // FCM Token
    private String fcmToken;
}
