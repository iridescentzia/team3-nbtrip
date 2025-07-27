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
import org.scoula.security.util.JwtProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/mypage")
public class MyPageController {
    @Autowired
    private MyPageService myPageService;

    @Autowired
    private JwtProcessor jwtProcessor;

    @Autowired
    private HttpServletRequest request;

    @Autowired
    private MemberService memberService;

    // 내 정보 조회 - GET /api/mypage
    @GetMapping
    public ResponseEntity<Map<String, Object>> getMyProfile(@AuthenticationPrincipal UserDetails userDetails) {
        Map<String, Object> response = new HashMap<>();
        try {
            String userEmail = userDetails.getUsername();
            log.info("마이페이지 - 내 정보 조회: {}", userEmail);
            Integer userId = extractUserIdFromToken();
            MemberResponseDTO userInfo = memberService.getMemberInfo(userId);
            response.put("success", true);
            response.put("data", userInfo);
            response.put("message", "내 정보 조회 성공");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("마이페이지 정보 조회 실패: {}", e.getMessage());
            response.put("success", false);
            response.put("message", "정보 조회에 실패했습니다.");
            return ResponseEntity.badRequest().body(response);
        }
    }

    // 프로필 정보 수정 - PUT /api/mypage
    @PutMapping
    public ResponseEntity<ApiResponse<String>> updateMyInfo(
            @RequestBody Map<String, Object> updateData,
            @AuthenticationPrincipal UserDetails userDetails) {
        try {
            String userEmail = userDetails.getUsername();
            log.info("마이페이지 - 회원정보 수정: {}, 수정데이터: {}", userEmail, updateData);
            Integer userId = extractUserIdFromToken();

            // 비밀번호 변경
            if (updateData.containsKey("currentPassword") && updateData.containsKey("newPassword")) {
                String currentPassword = (String) updateData.get("currentPassword");
                String newPassword = (String) updateData.get("newPassword");
                PasswordChangeRequestDTO passwordChangeDTO = new PasswordChangeRequestDTO();
                passwordChangeDTO.setCurrentPassword(currentPassword);
                passwordChangeDTO.setNewPassword(newPassword);
                boolean passwordResult = myPageService.changePassword(userId, passwordChangeDTO);
                if (!passwordResult) {
                    return ResponseEntity.badRequest()
                            .body(ApiResponse.error("현재 비밀번호가 일치하지 않습니다."));
                }
            }

            // 일반 정보 수정 (nickname, name, phone_number)
            if (updateData.containsKey("nickname") || updateData.containsKey("name") || updateData.containsKey("phoneNumber")) {
                UserUpdateRequestDTO updateRequest = new UserUpdateRequestDTO();
                if (updateData.containsKey("nickname")) {
                    updateRequest.setNickname((String) updateData.get("nickname"));
                }
                if (updateData.containsKey("name")) {
                    updateRequest.setName((String) updateData.get("name"));
                }
                if (updateData.containsKey("phoneNumber")) {
                    updateRequest.setPhoneNumber((String) updateData.get("phoneNumber"));
                }
                updateRequest.setUpdatedAt(LocalDateTime.now());
                boolean updateResult = myPageService.updateUserInfo(userId, updateRequest);
                if (!updateResult) {
                    return ResponseEntity.badRequest()
                            .body(ApiResponse.error("회원정보 수정에 실패했습니다."));
                }
            }
            return ResponseEntity.ok(ApiResponse.success("회원정보가 성공적으로 수정되었습니다."));
        } catch (Exception e) {
            log.error("회원정보 수정 실패: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("회원정보 수정 실패: " + e.getMessage()));
        }
    }

    // JWT에서 userId 추출 메서드
    private Integer extractUserIdFromToken() {
        try {
            String authHeader = request.getHeader("Authorization");
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                log.error("Authorization 헤더가 없거나 Bearer 토큰이 아닙니다.");
                throw new RuntimeException("JWT 토큰이 없습니다.");
            }
            String token = authHeader.substring(7);
            log.debug("추출된 JWT 토큰: {}", token.substring(0, 20) + "...");
            if (!jwtProcessor.validateToken(token)) {
                log.error("유효하지 않은 JWT 토큰입니다.");
                throw new RuntimeException("유효하지 않은 JWT 토큰입니다.");
            }
            Integer userId = jwtProcessor.getUserId(token);
            if (userId == null) {
                log.error("JWT 토큰에서 userId를 찾을 수 없습니다.");
                throw new RuntimeException("JWT 토큰에서 사용자 ID를 찾을 수 없습니다.");
            }
            log.debug("JWT에서 추출된 userId: {}", userId);
            return userId;
        } catch (Exception e) {
            log.error("JWT에서 userId 추출 실패: {}", e.getMessage());
            throw new RuntimeException("사용자 인증 정보를 찾을 수 없습니다: " + e.getMessage());
        }
    }
}
