package org.scoula.member.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class MemberLoginResponseDTO {
    private String accessToken;  // JWT 엑세스 토큰
    private Long expiresIn;  // 토큰 만료 시간(프론트)
    private MemberResponseDTO user;  // 로그인한 사용자 기본 정보(민감 정보 제외)
}
