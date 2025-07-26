package org.scoula.security.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
@Log4j2
public class JwtProcessor {
    @Value("${jwt.secret:travelGroupSecretKeyForJWT2024!@#$%^&*()}")
    private String jwtSecret;

    // 24시간 엑세스 토큰
    @Value("${jwt.access-token-expiry:86400000}")
    private long accessTokenExpiry;

    // JWT 토큰에서 이메일 추출
    public String getUsername(String token) {
        return getClaims(token).getSubject();
    }

    // JWT 토큰에서 userId 추출
    public Integer getUserId(String token) {
        return getClaims(token).get("userId", Integer.class);
    }

    // JWT 토큰 유효성 검증
    public boolean validateToken(String token) {
        try {
            getClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            log.warn("유효하지 않은 JWT 토큰: {}", e.getMessage());
            return false;
        }
    }

    // JWT 토큰에서 Claims 추출
    private Claims getClaims(String token) {
        SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes());

        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    // JWT 액세스 토큰 생성(24시간 유효)
    public String generateAccessToken(String email, Integer userId, String nickname) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + accessTokenExpiry);
        SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes());
        log.info("JWT 토큰 생성 - 이메일 : {}, 만료 시간 : 24시간", email);

        return Jwts.builder()
                .setSubject(email)
                .claim("userId", userId)
                .claim("nickname", nickname)
                .claim("tokenType", "access")
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(key)
                .compact();
    }
}