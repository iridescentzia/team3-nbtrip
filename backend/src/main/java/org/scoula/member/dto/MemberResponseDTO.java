package org.scoula.member.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class MemberResponseDTO {
    // 민감 정보(password, fcmToken, phoneNumber) 제외
    private int userId;
    private String email;
    private String nickname;
    private String name;
    private LocalDateTime createdAt;
}
