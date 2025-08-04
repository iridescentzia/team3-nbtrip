package org.scoula.member.dto;

import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MemberFcmTokenDTO {
    @NotBlank(message = "FCM 토큰을 입력해주세요.")
    @Size(max = 255, message = "FCM 토큰은 255자 이내여야 합니다.")
    private String fcmToken;
}
