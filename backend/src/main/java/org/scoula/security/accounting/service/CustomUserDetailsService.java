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

        // 사용자 정보 조회(email)
        MemberVO member = userDetailsMapper.getByEmail(email);

        // 사용자가 존재하지 않는 경우 예외 처리
        if (member == null) {
            log.warn("사용자 없음 : {}", email);
            throw new UsernameNotFoundException("가입된 사용자가 없습니다. : " + email);
        }

        log.info("로그인 성공 : {} (userId : {})", member.getNickname(), member.getUserId());
        return new CustomUser(member);
    }
}
