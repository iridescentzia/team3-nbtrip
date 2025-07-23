package org.scoula.security.filter;

import lombok.extern.log4j.Log4j2;
import org.scoula.security.accounting.dto.LoginDTO;
import org.scoula.security.handler.LoginSuccessHandler;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Log4j2
@Component
public class JwtUsernamePasswordAuthenticationFilter  extends UsernamePasswordAuthenticationFilter {
    public JwtUsernamePasswordAuthenticationFilter(
            AuthenticationManager authenticationManager,
            LoginSuccessHandler loginSuccessHandler,
            LoginFailureHandler loginFailureHandler) {
        super(authenticationManager);

        setFilterProcessesUrl("/api/auth/login");
        setAuthenticationSuccessHandler(loginSuccessHandler);  // 성공
        setAuthenticationFailureHandler(loginFailureHandler);  // 실패
        log.info("jwt 로그인 필터 초기화 완료")
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request,
                                                HttpServletResponse response) throws AuthenticationException {
        log.info("로그인 시도 - IP : {}", request.getRemoteAddr());

        // 1. HTTP Body에서 JSON 로그인 정보 추출
        LoginDTO loginDTO = LoginDTO.of(request);
        log.debug("로그인 정보 추출 완료 - 이메일 : {}", loginDTO.getEmail());

        // 2. 인증 토큰 생성
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                loginDTO.getEmail(),  // username 파라미터 = email
                loginDTO.getPassword()  // password 파라미터
        );
        return getAuthenticationManager().authenticate(authenticationToken);
    }
}
