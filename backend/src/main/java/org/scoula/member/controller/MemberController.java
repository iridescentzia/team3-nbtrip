package org.scoula.member.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.scoula.member.dto.*;
import org.scoula.member.exception.InvalidTokenException;
import org.scoula.member.exception.UserNotFoundException;
import org.scoula.member.service.MemberService;
import org.scoula.security.util.JwtProcessor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api")
public class MemberController {
    private final MemberService memberService;
    private final JwtProcessor jwtProcessor;

    // Firebase 서비스 워커 파일 서빙
    @GetMapping({"/firebase-messaging-sw.js", "/api/firebase-messaging-sw.js"})
    public ResponseEntity<String> getFirebaseServiceWorker() {
        try {
            log.info("🔥 Firebase 서비스 워커 요청");

            String content = """
                    // NbbangTrip Firebase Service Worker
                    console.log('🔥 NbbangTrip Firebase Service Worker 시작');
                    
                    importScripts('https://www.gstatic.com/firebasejs/9.22.0/firebase-app-compat.js');
                    importScripts('https://www.gstatic.com/firebasejs/9.22.0/firebase-messaging-compat.js');
                    
                    const firebaseConfig = {
                        apiKey: "AIzaSyDdGlnwurgQGdbDfPRbx0Gh2ZZ2G8AUBag",
                        authDomain: "nbtrip-push.firebaseapp.com",
                        projectId: "nbtrip-push",
                        storageBucket: "nbtrip-push.firebasestorage.app",
                        messagingSenderId: "312093298222",
                        appId: "1:312093298222:web:0a37dc9fdf32be875819bf"
                    };
                    
                    try {
                        firebase.initializeApp(firebaseConfig);
                        const messaging = firebase.messaging();
                        console.log('✅ Firebase 초기화 완료');
                        
                        messaging.onBackgroundMessage((payload) => {
                            console.log('📱 백그라운드 메시지:', payload);
                            const title = payload.notification?.title || '여행 그룹 알림';
                            const options = {
                                body: payload.notification?.body || '새로운 알림',
                                icon: '/favicon.ico',
                                tag: 'nbbang-trip'
                            };
                            self.registration.showNotification(title, options);
                        });
                        
                        self.addEventListener('notificationclick', (event) => {
                            event.notification.close();
                            event.waitUntil(clients.openWindow('/'));
                        });
                        
                    } catch (error) {
                        console.error('Firebase 초기화 실패:', error);
                    }
                    
                    self.addEventListener('install', () => {
                        console.log('Service Worker 설치');
                        self.skipWaiting();
                    });
                    
                    self.addEventListener('activate', () => {
                        console.log('Service Worker 활성화');
                        self.clients.claim();
                    });
                    
                    console.log('🎉 Firebase Service Worker 준비 완료');
                    """;

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType("application/javascript; charset=UTF-8"));
            headers.setCacheControl("no-cache, no-store, must-revalidate");
            headers.setPragma("no-cache");
            headers.setExpires(0);

            log.info("✅ Firebase 서비스 워커 서빙 완료");
            return new ResponseEntity<>(content, headers, HttpStatus.OK);

        } catch (Exception e) {
            log.error("❌ Firebase 서비스 워커 오류", e);
            return ResponseEntity.status(500).body("console.log('Service Worker Error');");
        }
    }

    // 닉네임 중복 확인(POST /api/users/check-nickname)
    @PostMapping("/users/check-nickname")
    public ResponseEntity<ApiResponse> checkNickname(@Valid @RequestBody MemberNicknameCheckDTO dto) {
        log.info("닉네임 중복 확인 요청 - 닉네임: {}", dto.getNickname());

        boolean exists = memberService.checkNicknameDuplicate(dto.getNickname());

        if (exists) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new ApiResponse(false, "이미 사용중인 닉네임입니다."));
        } else {
            return ResponseEntity.ok(new ApiResponse(true, "사용 가능한 닉네임입니다."));
        }
    }

    // 정보 조회(GET /api/users/{userId})
    @GetMapping("/users/{userId}")
    public ResponseEntity<?> getUserInfo(@PathVariable("userId") int userId) {
        log.info("회원 정보 조회 요청 - userId: {}", userId);
        try {
            MemberResponseDTO member = memberService.getMemberInfo(userId);
            return ResponseEntity.ok(member);
        } catch (UserNotFoundException e) {
            log.warn("유저 없음 - userId: {}, 오류: {}", userId, e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse(false, e.getMessage()));
        } catch (Exception e) {
            log.error("❌ 회원 정보 조회 실패 - userId: {}", userId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse(false, "서버 오류"));
        }
    }

    // FCM 토큰 갱신(PUT /api/users/fcm-token) - 여행 그룹 알림용
    @PutMapping("/users/fcm-token")
    public ResponseEntity<ApiResponse> updateFcmToken(
            @Valid @RequestBody MemberFcmTokenDTO memberFcmTokenDTO,
            BindingResult bindingResult,
            @RequestHeader("Authorization") String authorizationHeader) {

        // 유효성 검사
        if(bindingResult.hasErrors()) {
            String errorMessage = bindingResult.getFieldErrors().get(0).getDefaultMessage();
            log.warn("FCM 토큰 갱신 유효성 검사 실패: {}", errorMessage);
            return ResponseEntity.badRequest().body(new ApiResponse(false, errorMessage));
        }

        try {
            int userId = extractUserIdFromJwt(authorizationHeader);

            // FCM 토큰 유효성 추가 검증
            String fcmToken = memberFcmTokenDTO.getFcmToken();
            if (fcmToken == null || fcmToken.trim().isEmpty()) {
                log.warn("FCM 토큰 갱신 실패 - 빈 토큰, userId: {}", userId);
                return ResponseEntity.badRequest()
                        .body(new ApiResponse(false, "유효하지 않은 FCM 토큰입니다"));
            }

            if (fcmToken.length() < 50) {
                log.warn("FCM 토큰 갱신 실패 - 토큰 길이 부족, userId: {}, 길이: {}", userId, fcmToken.length());
                return ResponseEntity.badRequest()
                        .body(new ApiResponse(false, "FCM 토큰 형식이 올바르지 않습니다"));
            }

            memberService.updateFcmToken(userId, memberFcmTokenDTO);

            log.info("✅ FCM 토큰 갱신 성공 - userId: {}, 토큰: {}...",
                    userId, fcmToken.substring(0, 20));
            log.info("🔔 여행 그룹 실시간 알림 서비스 활성화 완료 - userId: {}", userId);

            return ResponseEntity.ok(new ApiResponse(true, "여행 그룹 알림 서비스 활성화 완료"));
        } catch (UserNotFoundException e) {
            log.warn("FCM 토큰 갱신 실패 - 사용자 없음: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse(false, e.getMessage()));
        } catch (InvalidTokenException e) {
            log.warn("FCM 토큰 갱신 실패 - JWT 토큰 문제: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ApiResponse(false, e.getMessage()));
        } catch (Exception e) {
            log.error("❌ FCM 토큰 갱신 서버 오류", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(false, "FCM 토큰 저장 중 오류가 발생했습니다"));
        }
    }

    // 사용자 검색(닉네임) (POST /api/users/search/{nickname})
    @GetMapping("/users/search/{nickname}")
    public ResponseEntity<List<MemberSearchResponseDTO>> searchUsersByNickname(@PathVariable("nickname") String nickname) {
        return ResponseEntity.ok(memberService.searchMembersByNickname(nickname));
    }

    // JWT 토큰에서 userId 추출 메서드
    private int extractUserIdFromJwt(String authorizationHeader) {
        if(authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            log.warn("JWT 토큰 추출 실패 - Authorization 헤더 없음 또는 형식 오류");
            throw new InvalidTokenException("JWT 토큰이 없습니다.");
        }

        String token = authorizationHeader.substring(7);
        if(!jwtProcessor.validateToken(token)) {
            log.warn("JWT 토큰 검증 실패 - 유효하지 않은 토큰");
            throw new InvalidTokenException("유효하지 않은 JWT 토큰입니다.");
        }

        Integer userId = jwtProcessor.getUserId(token);
        if(userId == null) {
            log.warn("JWT 토큰에서 userId 추출 실패");
            throw new InvalidTokenException("JWT 토큰에서 사용자 ID를 찾을 수 없습니다.");
        }

        return userId;
    }

    // 비밀번호 검증(POST /api/users/verify-password)
    @PostMapping("/users/verify-password")
    public ResponseEntity<ApiResponse> verifyPassword(
            @RequestHeader("Authorization") String authHeader,
            @Valid @RequestBody MemberPasswordDTO dto) {
        try {
            int userId = extractUserIdFromJwt(authHeader);
            boolean verified = memberService.verifyPassword(userId, dto.getCurrentPassword());

            if (verified) {
                log.info("비밀번호 검증 성공 - userId: {}", userId);
                return ResponseEntity.ok(new ApiResponse(true, "비밀번호 확인 성공"));
            } else {
                log.warn("비밀번호 검증 실패 - userId: {}", userId);
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new ApiResponse(false, "비밀번호가 일치하지 않습니다"));
            }
        } catch (InvalidTokenException e) {
            log.warn("비밀번호 검증 실패 - JWT 토큰 문제: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ApiResponse(false, e.getMessage()));
        } catch (Exception e) {
            log.error("❌ 비밀번호 검증 서버 오류", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(false, "서버 오류가 발생했습니다"));
        }
    }
}
