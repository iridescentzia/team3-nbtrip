package org.scoula.security.filter;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.SignatureException;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.scoula.security.util.JsonResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
@Log4j2
public class AuthenticationErrorFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        try {
            filterChain.doFilter(request, response);
        } catch (ExpiredJwtException e) {
            log.warn("토큰 만료 - URI: {}, 사용자: {}", request.getRequestURI(), e.getClaims().getSubject());
            JsonResponse.sendError(response, HttpStatus.UNAUTHORIZED, "토큰의 유효시간이 지났습니다. 다시 로그인해주세요.");
        } catch (UnsupportedJwtException e) {
            log.warn("지원하지 않는 JWT 토큰 형식 - URI: {}", request.getRequestURI());
            JsonResponse.sendError(response, HttpStatus.UNAUTHORIZED, "지원하지 않는 토큰 형식입니다.");
        } catch (MalformedJwtException e) {
            log.warn("잘못된 JWT 토큰 형식 - URI: {}", request.getRequestURI());
            JsonResponse.sendError(response, HttpStatus.UNAUTHORIZED, "잘못된 토큰 형식입니다.");
        } catch (SignatureException e) {
            log.warn("JWT 서명 검증 실패 - URI: {}", request.getRequestURI());
            JsonResponse.sendError(response, HttpStatus.UNAUTHORIZED, "토큰 서명이 유효하지 않습니다.");
        } catch (IllegalArgumentException e) {
            log.warn("JWT 토큰이 비어있음 - URI: {}", request.getRequestURI());
            JsonResponse.sendError(response, HttpStatus.UNAUTHORIZED, "토큰이 비어있습니다.");
        } catch (ServletException e) {
            log.error("서블릿 예외 발생 - URI: {}, 오류: {}", request.getRequestURI(), e.getMessage());
            JsonResponse.sendError(response, HttpStatus.INTERNAL_SERVER_ERROR, "서버 내부 오류가 발생했습니다.");
        } catch (Exception e) {
            log.error("예상치 못한 오류 발생 - URI: {}, 오류: {}", request.getRequestURI(), e.getMessage());
            JsonResponse.sendError(response, HttpStatus.INTERNAL_SERVER_ERROR, "인증 처리 중 오류가 발생했습니다.");
        }
    }
}
