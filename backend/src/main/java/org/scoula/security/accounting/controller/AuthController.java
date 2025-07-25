package org.scoula.security.accounting.controller;

import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/auth")
@Log4j2
public class AuthController {

    // 로그아웃 요청 처리
    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request) {
        String authorizationHeader = request.getHeader("Authorization");
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            log.warn("유효하지 않은 Authorization 헤더: {}", authorizationHeader);
            return ResponseEntity.status(400).body("{\"message\": \"잘못된 요청입니다.\"}");
        }
        String token = authorizationHeader.substring(7);  // "Bearer " 이후 토큰만 추출
        log.info("로그아웃 요청 - 토큰: {}", token);
        return ResponseEntity.ok().body("{\"message\": \"로그아웃이 완료되었습니다.\"}");
    }
}