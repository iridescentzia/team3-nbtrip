package org.scoula.security.accounting.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.scoula.member.dto.ApiResponse;
import org.scoula.member.dto.MemberDTO;
import org.scoula.member.exception.DuplicateEmailException;
import org.scoula.member.exception.DuplicateNicknameException;
import org.scoula.member.exception.UserNotFoundException;
import org.scoula.member.service.EmailService;
import org.scoula.member.service.MemberService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Log4j2
public class AuthController {
    private final MemberService memberService;
    private final EmailService emailService;

    // 회원가입(POST /api/auth/register) - FCM 토큰 선택사항 처리
    @PostMapping(value = "/register", produces = MediaType.APPLICATION_JSON_VALUE + ";charset=UTF-8")
    public ResponseEntity<ApiResponse> register(@Valid @RequestBody MemberDTO memberDTO, BindingResult bindingResult) {
        log.info("🔐 N빵 트립 회원가입 요청 - 이메일: {}", memberDTO.getEmail());

        // 요청 파라미터 유효성 검사
        if (bindingResult.hasErrors()) {
            String errorMessage = bindingResult.getFieldErrors().get(0).getDefaultMessage();
            log.warn("회원가입 유효성 검사 실패: {}", errorMessage);
            return ResponseEntity.badRequest().body(new ApiResponse(false, errorMessage));
        }
        try {
            boolean isEmailVerified = emailService.isEmailVerifiedInMemory(memberDTO.getEmail());

            if (!isEmailVerified) {
                log.warn("이메일 인증이 완료되지 않음: {}", memberDTO.getEmail());
                return ResponseEntity.badRequest()
                        .body(new ApiResponse(false, "이메일 인증을 먼저 완료해주세요."));
            }
            ApiResponse response = memberService.registerMember(memberDTO);
            emailService.clearEmailVerificationStatus(memberDTO.getEmail());
            log.info("✅ 회원가입 성공 - 이메일: {}", memberDTO.getEmail());
            return ResponseEntity.ok(new ApiResponse(true, "회원가입이 완료되었습니다!"));

        } catch (DuplicateEmailException | DuplicateNicknameException e) {
            log.warn("회원가입 실패 - 중복 데이터: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT).body(new ApiResponse(false, e.getMessage()));
        } catch (Exception e) {
            log.error("❌ 회원가입 서버 오류 - 이메일: {}, 오류: {}", memberDTO.getEmail(), e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(false, "서버 오류가 발생했습니다"));
        }
    }

    // 이메일 인증번호 발송
    @PostMapping(value = "/send-verification", produces = MediaType.APPLICATION_JSON_VALUE + ";charset=UTF-8")
    public ResponseEntity<ApiResponse> sendVerificationOnly(@Valid @RequestBody Map<String, String> request) {
        String email = request.get("email");
        log.info("이메일 인증번호 발송 요청 - 이메일: {}", email);
        try {
            try {
                memberService.findByEmail(email);
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body(new ApiResponse(false, "이미 가입된 이메일입니다."));
            } catch (UserNotFoundException e) {
            }
            emailService.sendVerificationCodeAsync(email); // 🎯 비동기 메서드 호출
            log.info("✅ 인증번호 발송 요청 처리 완료 (즉시 응답) - 이메일: {}", email);
            return ResponseEntity.ok(new ApiResponse(true, "인증번호를 발송했습니다."));
        } catch (Exception e) {
            log.error("❌ 인증번호 발송 오류 - 이메일: {}", email, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(false, "인증번호 발송에 실패했습니다."));
        }
    }

    // 이메일 인증 확인
    @PostMapping(value = "/verify-email", produces = MediaType.APPLICATION_JSON_VALUE + ";charset=UTF-8")
    public ResponseEntity<ApiResponse> verifyEmail(@Valid @RequestBody Map<String, String> request) {
        String email = request.get("email");
        String code = request.get("code");
        log.info("이메일 인증 요청 - 이메일: {}, 코드: {}", email, code);
        try {
            boolean isValid = emailService.verifyCodeByEmail(email, code);
            if (isValid) {
                try {
                    memberService.updateEmailVerified(email, true);
                    log.info("✅ 기존 회원 이메일 인증 완료 - 이메일: {}", email);
                } catch (UserNotFoundException e) {
                    log.info("✅ 회원가입 중 이메일 인증 완료 - 이메일: {}", email);
                    emailService.markEmailAsVerified(email);
                }
                return ResponseEntity.ok(new ApiResponse(true, "이메일 인증이 완료되었습니다!"));
            } else {
                log.warn("❌ 이메일 인증 실패 - 잘못된 인증번호");
                return ResponseEntity.badRequest()
                        .body(new ApiResponse(false, "인증번호가 올바르지 않거나 만료되었습니다."));
            }
        } catch (Exception e) {
            log.error("💥 이메일 인증 처리 중 예외 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(false, "인증 처리 중 오류가 발생했습니다."));
        }
    }

    // 인증번호 재발송
    @PostMapping(value = "/resend-verification", produces = MediaType.APPLICATION_JSON_VALUE + ";charset=UTF-8")
    public ResponseEntity<ApiResponse> resendVerification(@Valid @RequestBody Map<String, String> request) {
        String email = request.get("email");
        log.info("인증번호 재발송 요청 - 이메일: {}", email);
        try {
            emailService.resendVerificationCodeByEmail(email);

            log.info("✅ 인증번호 재발송 완료 - 이메일: {}", email);
            return ResponseEntity.ok(new ApiResponse(true, "인증번호를 다시 발송했습니다."));

        } catch (Exception e) {
            log.error("💥 인증번호 재발송 중 예외 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(false, "재발송 중 오류가 발생했습니다."));
        }
    }

    // 로그아웃
    @PostMapping(value = "/logout", produces = MediaType.APPLICATION_JSON_VALUE + ";charset=UTF-8")
    public ResponseEntity<?> logout(HttpServletRequest request) {
        String authorizationHeader = request.getHeader("Authorization");
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            log.warn("유효하지 않은 Authorization 헤더: {}", authorizationHeader);
            return ResponseEntity
                    .status(400)
                    .contentType(MediaType.parseMediaType("application/json;charset=UTF-8"))
                    .body("{\"message\": \"잘못된 요청입니다.\"}");
        }
        String token = authorizationHeader.substring(7);
        log.info("로그아웃 요청 - 토큰: {}", token);
        return ResponseEntity
                .ok()
                .contentType(MediaType.parseMediaType("application/json;charset=UTF-8"))
                .body("{\"message\": \"로그아웃이 완료되었습니다.\"}");
    }
}