package org.scoula.member.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;
import org.scoula.security.util.JwtProcessor;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;

@SpringJUnitConfig(locations = {
        "classpath:spring/root-context.xml",
        "classpath:spring/servlet-context.xml",
        "classpath:spring/security-context.xml"
})
@WebAppConfiguration
@Transactional
@DisplayName("MemberController 통합 테스트 (24시간 토큰)")
class MemberControllerTest {

    @Autowired
    private WebApplicationContext wac;

    @Autowired
    private JwtProcessor jwtProcessor;
    private MockMvc mockMvc;
    private ObjectMapper objectMapper = new ObjectMapper();
    private static final long TOKEN_24_HOURS = 86400000L;  // 24시간

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();
    }

    // 회원가입 성공 테스트
    @Test
    @DisplayName("회원가입 API 성공 테스트")
    void testRegister_성공() throws Exception {
        // Given: 유효한 회원가입 데이터
        String memberJson = """
            {
                "email": "newuser@nbtrip.com",
                "password": "TravelPass123!",
                "passwordConfirm": "TravelPass123!",
                "nickname": "여행러123",
                "name": "김여행",
                "phoneNumber": "010-9999-8888",
                "fcmToken": "fcm-token-travel-new"
            }
            """;

        // When & Then: API 호출 및 검증
        mockMvc.perform(post("/api/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(memberJson))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("회원가입이 완료되었습니다."))
                .andDo(print());
    }

    // 회원가입 유효성 검사 실패 테스트
    @Test
    @DisplayName("회원가입 유효성 검사 실패 테스트")
    void testRegister_유효성검사실패() throws Exception {
        // Given: 잘못된 이메일 형식 및 짧은 비밀번호
        String invalidMemberJson = """
            {
                "email": "invalid-email",
                "password": "123",
                "passwordConfirm": "456",
                "nickname": "",
                "name": "",
                "phoneNumber": "wrong-format"
            }
            """;

        // When & Then: 400 Bad Request 응답 확인
        mockMvc.perform(post("/api/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidMemberJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andDo(print());
    }

    // 회원가입 중복 이메일 테스트
    @Test
    @DisplayName("회원가입 중복 이메일 테스트")
    void testRegister_중복이메일() throws Exception {
        // Given: 이미 존재하는 이메일로 회원가입 시도
        String duplicateEmailJson = """
            {
                "email": "existing@nbtrip.com",
                "password": "ValidPass123!",
                "passwordConfirm": "ValidPass123!",
                "nickname": "중복테스터",
                "name": "중복테스트",
                "phoneNumber": "010-1111-2222"
            }
            """;

        // 먼저 회원가입 (성공)
        mockMvc.perform(post("/api/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(duplicateEmailJson))
                .andExpect(status().isOk());

        // When & Then: 같은 이메일로 다시 회원가입 시도 (실패)
        mockMvc.perform(post("/api/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(duplicateEmailJson))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("이미 가입된 이메일입니다."))
                .andDo(print());
    }

    // 회원 정보 조회 - 인증 없이 접근 테스트
    @Test
    @DisplayName("회원 정보 조회 - 인증 없이 접근 시 401 응답")
    void testGetUserInfo_인증없이접근() throws Exception {
        // When & Then: JWT 토큰 없이 접근 시 401 응답
        mockMvc.perform(get("/api/users/1"))
                .andExpect(status().isUnauthorized())
                .andDo(print());
    }

    // 회원 정보 조회 - 유효한 24시간 JWT 토큰으로 접근 테스트
    @Test
    @DisplayName("회원 정보 조회 - 유효한 24시간 토큰으로 접근 성공")
    void testGetUserInfo_유효한24시간토큰() throws Exception {
        // Given: 유효한 24시간 JWT 토큰 생성
        String valid24HToken = jwtProcessor.generateAccessToken("test@nbtrip.com", 1, "테스터");

        // When & Then: 유효한 24시간 토큰으로 접근 시 성공
        mockMvc.perform(get("/api/users/1")
                        .header("Authorization", "Bearer " + valid24HToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(1))
                .andExpect(jsonPath("$.email").value("test@nbtrip.com"))
                .andDo(print());
    }

    // 회원 정보 조회 - 잘못된 JWT 토큰으로 접근 테스트
    @Test
    @DisplayName("회원 정보 조회 - 잘못된 토큰으로 접근 시 401 응답")
    void testGetUserInfo_잘못된토큰() throws Exception {
        // Given: 잘못된 JWT 토큰
        String invalidToken = "invalid.jwt.token";

        // When & Then: 잘못된 토큰으로 접근 시 401 응답
        mockMvc.perform(get("/api/users/1")
                        .header("Authorization", "Bearer " + invalidToken))
                .andExpect(status().isUnauthorized())
                .andDo(print());
    }

    // FCM 토큰 갱신 성공 테스트 - 24시간 토큰 사용
    @Test
    @DisplayName("FCM 토큰 갱신 성공 - 24시간 토큰 사용")
    void testUpdateFcmToken_24시간토큰성공() throws Exception {
        // Given: 유효한 24시간 JWT 토큰과 FCM 토큰 데이터
        String valid24HToken = jwtProcessor.generateAccessToken("traveler@nbtrip.com", 1, "여행러");
        String fcmTokenJson = """
            {
                "fcmToken": "new-travel-fcm-token-12345"
            }
            """;

        // When & Then: FCM 토큰 갱신 성공
        mockMvc.perform(put("/api/users/fcm-token")
                        .header("Authorization", "Bearer " + valid24HToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(fcmTokenJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("FCM 토큰이 갱신되었습니다."))
                .andDo(print());
    }

    // FCM 토큰 갱신 - 인증 없이 접근 테스트
    @Test
    @DisplayName("FCM 토큰 갱신 - 인증 없이 접근 시 401 응답")
    void testUpdateFcmToken_인증없음() throws Exception {
        // Given: FCM 토큰 데이터 (인증 헤더 없음)
        String fcmTokenJson = """
            {
                "fcmToken": "new-fcm-token-12345"
            }
            """;

        // When & Then: 인증 없이 접근 시 401 응답
        mockMvc.perform(put("/api/users/fcm-token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(fcmTokenJson))
                .andExpect(status().isUnauthorized())
                .andDo(print());
    }

    // 여행 그룹용 시나리오 테스트 - 24시간 토큰 활용
    @Test
    @DisplayName("여행 그룹 시나리오 - 24시간 토큰으로 연속 활동")
    void testTravelGroupScenario_24시간토큰활용() throws Exception {
        // Given: 여행 그룹 회원 등록
        String travelMemberJson = """
            {
                "email": "groupleader@nbtrip.com",
                "password": "GroupLeader123!",
                "passwordConfirm": "GroupLeader123!",
                "nickname": "그룹장",
                "name": "김그룹",
                "phoneNumber": "010-1234-5678",
                "fcmToken": "group-leader-fcm-token"
            }
            """;

        // 회원가입
        mockMvc.perform(post("/api/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(travelMemberJson))
                .andExpect(status().isOk());

        // 24시간 토큰 생성 (로그인 성공 시나리오)
        String longTermToken = jwtProcessor.generateAccessToken("groupleader@nbtrip.com", 1, "그룹장");

        // When & Then: 24시간 동안 사용할 수 있는 기능들 테스트
        // 1. 회원 정보 조회 (OK)
        mockMvc.perform(get("/api/users/1")
                        .header("Authorization", "Bearer " + longTermToken))
                .andExpect(status().isOk());

        // 2. FCM 토큰 갱신 (OK)
        mockMvc.perform(put("/api/users/fcm-token")
                        .header("Authorization", "Bearer " + longTermToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"fcmToken\": \"updated-group-fcm\"}"))
                .andExpect(status().isOk());
    }

    // 잘못된 JSON 포맷 요청 테스트
    @Test
    @DisplayName("잘못된 JSON 포맷 요청 시 400 응답")
    void testRegister_잘못된JSON() throws Exception {
        // Given: 잘못된 JSON 포맷
        String malformedJson = "{ invalid json }";

        // When & Then: 400 Bad Request 응답
        mockMvc.perform(post("/api/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(malformedJson))
                .andExpect(status().isBadRequest())
                .andDo(print());
    }
}
