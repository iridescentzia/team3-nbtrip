package org.scoula.security.accounting.domain;

import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;
import java.util.List;

@Getter
public class CustomUser extends User {
    private final MemberVO member;  // 사용자 전체 정보

    // 메인 생성자
    public CustomUser(MemberVO memberVO) {
        super(
                memberVO.getEmail(),  // 로그인 시 사용 계정
                memberVO.getPassword(),  // 해시된 비밀번호
                List.of(new SimpleGrantedAuthority("ROLE_USER"))  // 기본 권한("ROLE_USER" 고정)
        );
        this.member = memberVO;
    }

    // 보조 생성자
    public CustomUser(String username, String password, Collection<? extends GrantedAuthority> authorities) {
        super(username, password, authorities);
        this.member = null;
    }

    // 사용자 정보 편의 메서드
    public Integer getUserId() {  // 데이터 타입 변경(Long -> Integer)
        return member != null ? member.getUserId() : null;
    }
    public String getNickname() { return member != null ? member.getNickname() : null; }
    public String getPhoneNumber() {
        return member != null ? member.getPhoneNumber() : null;
    }
    public String getName() {
        return member != null ? member.getName() : null;
    }
}
