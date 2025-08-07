package org.scoula.mypage.controller;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.scoula.member.dto.MemberResponseDTO;
import org.scoula.member.service.MemberService;
import org.scoula.mypage.dto.ApiResponse;
import org.scoula.mypage.dto.PasswordChangeRequestDTO;
import org.scoula.mypage.dto.UserUpdateRequestDTO;
import org.scoula.mypage.service.MyPageService;
import org.scoula.security.accounting.domain.CustomUser;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/mypage")
public class MyPageController {
    private final MyPageService myPageService;
    private final MemberService memberService;
//    private final JwtProcessor jwtProcessor;
//    private final HttpServletRequest request;

    // 내 정보 조회(GET /api/mypage)
    @GetMapping
    public ResponseEntity<ApiResponse<MemberResponseDTO>> getMyProfile(@AuthenticationPrincipal CustomUser customUser) {
        try {
            if (customUser == null) {
                log.warn("인증 정보가 없습니다. (customUser == null)");
                return ResponseEntity.status(401).body(ApiResponse.error("로그인이 필요합니다."));
            }

            String userEmail = customUser.getUsername();
            Integer userId = customUser.getUserId();
            log.info("마이페이지 - 내 정보 조회: email = {}, userId = {}", userEmail, userId);

            MemberResponseDTO userInfo = memberService.getMemberInfo(userId);
            return ResponseEntity.ok(ApiResponse.success(userInfo));
        } catch (Exception e) {
            log.error("마이페이지 정보 조회 실패: {}", e.getMessage());
            return ResponseEntity.badRequest().body(ApiResponse.error("정보 조회에 실패했습니다."));
        }
    }

    // 프로필 정보 수정(PUT /api/mypage)
    @PutMapping
    public ResponseEntity<ApiResponse<String>> updateMyInfo(
            @AuthenticationPrincipal CustomUser customUser,
            @Valid @RequestBody UserUpdateRequestDTO updateRequest) {
        try {
            if (customUser == null) {
                return ResponseEntity.status(401).body(ApiResponse.error("로그인이 필요합니다."));
            }

            String userEmail = customUser.getUsername();
            Integer userId = customUser.getUserId();
            log.info("회원정보 수정 요청: email={}, userId={}", userEmail, userId);

            if (updateRequest.getPassword() != null && !updateRequest.getPassword().trim().isEmpty()) {
                log.info("비밀번호 변경도 함께 요청됨");
            }

            boolean result = myPageService.updateUserInfo(userId, updateRequest);
            if (!result) {
                return ResponseEntity.badRequest().body(ApiResponse.error("회원정보 수정 실패"));
            }

            return ResponseEntity.ok(ApiResponse.success("회원정보가 성공적으로 수정되었습니다."));
        } catch (Exception e) {
            log.error("회원정보 수정 실패: {}", e.getMessage());
            return ResponseEntity.badRequest().body(ApiResponse.error("회원정보 수정 중 오류 발생"));
        }
    }


    // 비밀번호 변경(PUT /api/mypage)
    @PutMapping("/password")
    public ResponseEntity<ApiResponse<String>> changePassword(
            @AuthenticationPrincipal CustomUser customUser,
            @Valid @RequestBody PasswordChangeRequestDTO passwordDTO) {
        try {
            if (customUser == null) {
                return ResponseEntity.status(401).body(ApiResponse.error("로그인이 필요합니다."));
            }

            String userEmail = customUser.getUsername();
            Integer userId = customUser.getUserId();
            log.info("비밀번호 변경 요청: email={}, userId={}", userEmail, userId);

            boolean result = myPageService.changePassword(userId, passwordDTO);
            if (!result) {
                return ResponseEntity.badRequest().body(ApiResponse.error("현재 비밀번호가 일치하지 않습니다."));
            }

            return ResponseEntity.ok(ApiResponse.success("비밀번호가 성공적으로 변경되었습니다."));
        } catch (Exception e) {
            log.error("비밀번호 변경 실패: {}", e.getMessage());
            return ResponseEntity.badRequest().body(ApiResponse.error("비밀번호 변경 중 오류 발생"));
        }
    }

    // 비밀번호 검증
    @PostMapping("/verify-password")
    public ResponseEntity<ApiResponse<String>> verifyPassword(
            @AuthenticationPrincipal CustomUser customUser,
            @RequestBody Map<String, String> body) {
        try {
            if (customUser == null) {
                return ResponseEntity.status(401).body(ApiResponse.error("로그인이 필요합니다."));
            }

            Integer userId = customUser.getUserId();
            String currentPassword = body.get("currentPassword");
            log.info("비밀번호 검증 요청: userId={}", userId);

            boolean isMatch = myPageService.verifyCurrentPassword(userId, currentPassword);
            if (isMatch) {
                return ResponseEntity.ok(ApiResponse.success("비밀번호가 일치합니다."));
            } else {
                return ResponseEntity.status(401).body(ApiResponse.error("비밀번호가 일치하지 않습니다."));
            }
        } catch (Exception e) {
            log.error("비밀번호 확인 실패: {}", e.getMessage());
            return ResponseEntity.badRequest().body(ApiResponse.error("비밀번호 확인 중 오류 발생"));
        }
    }
}