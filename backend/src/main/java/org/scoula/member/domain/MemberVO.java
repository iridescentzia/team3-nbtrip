package org.scoula.member.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class MemberVO {
    private int userId;
    private String email;
    private String password;
    private String nickname;
    private LocalDateTime createdAt;
    private String fcmToken;
    private String name;
    private String phoneNumber;
}
