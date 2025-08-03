package org.scoula.member.domain;

import lombok.Builder;
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
    private LocalDateTime updatedAt;
    private String fcmToken;
    private String name;
    private String phoneNumber;

    @Builder
    public MemberVO(int userId, String email, String password, String nickname, LocalDateTime createdAt, LocalDateTime updatedAt, String fcmToken, String name, String phoneNumber) {
        this.userId = userId;
        this.email = email;
        this.password = password;
        this.nickname = nickname;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.fcmToken = fcmToken;
        this.name = name;
        this.phoneNumber = phoneNumber;
    }
}
