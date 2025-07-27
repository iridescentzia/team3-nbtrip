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
    private String fcmToken;
    private String name;
    private String phoneNumber;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private List<AuthVO> authList;  // 사용자 권한 목록
}
