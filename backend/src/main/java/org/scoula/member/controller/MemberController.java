package org.scoula.member.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.scoula.member.dto.ApiResponse;
import org.scoula.member.dto.MemberDTO;
import org.scoula.member.dto.MemberFcmTokenDTO;
import org.scoula.member.dto.MemberResponseDTO;
import org.scoula.member.exception.DuplicateEmailException;
import org.scoula.member.exception.DuplicateNicknameException;
import org.scoula.member.exception.InvalidTokenException;
import org.scoula.member.exception.UserNotFoundException;
import org.scoula.member.service.MemberService;
import org.scoula.security.util.JwtProcessor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Slf4j
@RequiredArgsConstructor
@RestControllerAdvice
@RequestMapping("/api")
public class MemberController {
    private final MemberService memberService;
    private final JwtProcessor jwtProcessor;

    // 1. 회원가입(POST /api/user/register)
    @PostMapping("/users/register")
    public ResponseEntity<ApiResponse> register(@Valid @RequestBody MemberDTO memberDTO, BindingResult bindingResult) {
        log.info("회원가입 요청 - 이메일 : {}", memberDTO.getEmail());

        // 요청 파라미터 유효성 검사
        if(bindingResult.hasErrors()) {
            String errorMessage = bindingResult.getFieldErrors().get(0).getDefaultMessage();
            return ResponseEntity.badRequest().body(new ApiResponse(false, errorMessage));
        } try {
            ApiResponse response = memberService.registerMember(memberDTO);
            return ResponseEntity.ok(response);
        } catch (DuplicateEmailException | DuplicateNicknameException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(new ApiResponse(false, e.getMessage()));
        } catch (Exception e) {
            log.error("회원가입 에러 : {}", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse(false, "서버 오류"));
        }
    }

    // 2. 정보 조회(GET /api/users/{userId})
    @GetMapping("/users/{userId}")
    public ResponseEntity<?> getUserInfo(@PathVariable("userId") int userId) {
        try {
            MemberResponseDTO member = memberService.getMemberInfo(userId);
            return ResponseEntity.ok(member);
        } catch (UserNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse(false, e.getMessage()));
        } catch (Exception e) {
            log.error("회원 정보 조회 실패", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse(false, "서버 오류"));
        }
    }

    // 3. FCM 토큰 갱신(PUT /api/users/fcm-token)
    @PutMapping("/users/fcm-token")
    public ResponseEntity<ApiResponse> updateFcmToken(
            @Valid @RequestBody MemberFcmTokenDTO memberFcmTokenDTO, BindingResult bindingResult,
            @RequestHeader("Authorization") String authorizationHeader) {

        // 유효성 검사
        if(bindingResult.hasErrors()) {
            String errorMessage = bindingResult.getFieldErrors().get(0).getDefaultMessage();
            return ResponseEntity.badRequest().body(new ApiResponse(false, errorMessage));
        } try {
            int userId = extractUserIdFromJwt(authorizationHeader);
            memberService.updateFcmToken(userId, memberFcmTokenDTO);
            return ResponseEntity.ok(new ApiResponse(true, "FCM 토큰 갱신 완료"));
        } catch (UserNotFoundException | InvalidTokenException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(new ApiResponse(false, e.getMessage()));
        } catch (Exception e) {
            log.error("FCM 토큰 갱신 실패", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse(false, "서버 오류"));
        }
    }

    // JWT 토큰에서 userId 추출 메서드
    private int extractUserIdFromJwt(String authorizationHeader) {
        if(authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) { throw new InvalidTokenException("JWT 토큰이 없습니다."); };
        String token = authorizationHeader.substring(7);
        if(!jwtProcessor.validateToken(token)) { throw new InvalidTokenException("유효하지 않은 JWT 토큰입니다."); }
        Integer userId = jwtProcessor.getUserId(token);
        if(userId == null) { throw new InvalidTokenException("JWT 토큰에서 사용자 ID를 찾을 수 없습니다."); }
        return userId;
    }
}