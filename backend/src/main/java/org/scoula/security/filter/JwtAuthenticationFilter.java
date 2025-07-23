package org.scoula.security.filter;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.scoula.security.util.JwtProcessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
@Log4j2
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    public static final String AUTHORIZATION_HEADER = "Authorization";
    public static final String BEARER_PREFIX = "Bearer ";

    private final JwtProcessor jwtProcessor;
    private final UserDetailsService userDetailsService;

    // JWT 토큰에서 이메일 추출 및 인증 정보 생성(username => email 값 의미)
    private Authentication getAuthentication(String token) {
        String email = jwtProcessor.getUsername(token);
        log.debug("JWT에서 추출한 이메일 : {}", email);

        UserDetails userDetails = userDetailsService.loadUserByUsername(email);
        return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        // 1. 헤더에서 Authorization 값 추출
        String bearerToken = request.getHeader(AUTHORIZATION_HEADER);

        // 2. Bearer 토큰 형식 검증 및 JWT 토큰 추출
        if (bearerToken != null && bearerToken.startsWith(BEARER_PREFIX)) {
            String token = bearerToken.substring(BEARER_PREFIX.length());
            log.debug("JWT 토큰 감지 - URI : {}", request.getRequestURI());

            // 3. JWT 토큰 유효성 검증
            if (jwtProcessor.validateToken(token)) {
                // 4. Authentication 객체 생성
                Authentication authentication = getAuthentication(token);

                // 5. SecurityContext에 인증 정보 설정
                SecurityContextHolder.getContext().setAuthentication(authentication);
                log.debug("JWT 인증 성공 - 이메일 : {}", authentication.getName());
            } else {
                log.warn("유효하지 않은 JWT 토큰 - URI : {}", request.getRequestURI());
            }
        }

        // 다음 필터로 요청 전달 (반드시 실행되어야 함)
        filterChain.doFilter(request, response);
    }

    // 필터 적용 제외
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String path = request.getRequestURI();

        return path.startsWith("/assets") ||
                path.startsWith("/api/health") ||
                path.startsWith("/api/status") ||
                path.startsWith("/swagger-ui") ||
                path.startsWith("/v3/api-docs") ||
                path.equals("/api/auth/login") ||
                path.equals("/api/users/register") ||
                (path.startsWith("/api/merchants/") && request.getMethod().equals("GET"));
    }
}

