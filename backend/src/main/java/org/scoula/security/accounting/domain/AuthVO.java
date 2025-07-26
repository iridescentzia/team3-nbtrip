package org.scoula.security.accounting.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthVO implements GrantedAuthority {
    private Long userId;  // 사용자 식별자
    private String auth;  // 권한명

    public AuthVO(String auth) {
        this.auth = auth;
    }

    @Override
    public String getAuthority() {
        return auth;
    }
}
