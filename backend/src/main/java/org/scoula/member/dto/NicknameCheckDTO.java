package org.scoula.member.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Getter
@Setter
@NoArgsConstructor
public class NicknameCheckDTO {
    @NotBlank(message = "닉네임을 입력해주세요.")
    @Size(min = 2, max = 100, message = "닉네임은 2-100자 사이여야 합니다.")
    private String nickname;
}
