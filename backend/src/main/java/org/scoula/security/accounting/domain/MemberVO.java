package org.scoula.security.accounting.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class MemberVO {
    private int userId;
    private String email;
    private String password;
    private String nickname;
    private LocalDateTime createdAt;
    private String fcmToken;
    private String name;
    private String phoneNumber;

    private List<AuthVO> authList;
}
