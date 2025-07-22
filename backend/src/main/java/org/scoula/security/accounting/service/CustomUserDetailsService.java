package org.scoula.security.accounting.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.scoula.security.accounting.domain.CustomUser;
import org.scoula.security.accounting.domain.MemberVO;
import org.scoula.security.accounting.mapper.UserDetailsMapper;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

@Component
@Log4j2
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
    private final UserDetailsMapper userDetailsMapper;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        log.info("로그인 요청 이메일 : {}", email);

        MemberVO member = userDetailsMapper.getByEmail(email);

        if (member == null) {
            log.warn("사용자 없음 : {}", email);
            throw new UsernameNotFoundException("가입된 사용자가 없습니다. : " + email);
        }

        if(!member.isEnabled()) {
            log.warn("비활성 사용자 : {}", email);
            throw new UsernameNotFoundException("비활성화된 계정입니다.");
        }

        log.info("로그인 성공 : {} (userId : {})", member.getNickname(), member.getUserId());
        return new CustomUser(member);
    }
}
