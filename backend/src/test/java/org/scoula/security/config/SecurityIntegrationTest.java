package org.scoula.security.config;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.scoula.security.util.JwtProcessor;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringJUnitConfig(locations = {
        "classpath:spring/root-context.xml",
        "classpath:spring/servlet-context.xml",
        "classpath:spring/security-context.xml"
})
@WebAppConfiguration
@DisplayName("Spring Security 통합 테스트 (24시간 토큰)")
class SecurityIntegrationTest {

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private JwtProcessor jwtProcessor;

    private MockMvc mockMvc;

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())  // Spring Security 적용
                .build();
    }

    // 공개 API 접근 테스트 (인증 불필요)
    @Test
    @DisplayName("공개 API 접근 가능 테스트")
    void testPublicEndpoints_접근가능() throws Exception {
        // 회원가입 API - 인증 불필요
        mockMvc.perform(post("/api/users/register"))
                .andExpect(status().isBadRequest()); // 파라미터 없어서 400, 하지만 인증은 통과

        // 로그인 API - 인증 불필요
        mockMvc.perform(post("/api/auth/login"))
                .andExpect(status().isBadRequest()); // 파라미터 없어서 400, 하지만 인증은 통과

        // 가맹점 조회 API - 인증 불필요
        mockMvc.perform(get("/api/merchants/1"))
                .andExpect(status().isNotFound()); // 데이터 없어서 404, 하지만 인증은 통과
    }

    // 보호된 API 접근 테스트 (인증 필요)
    @Test
    @DisplayName("보호된 API - 인증 없이 접근 시 401 응답")
    void testProtectedEndpoints_인증없이접근() throws Exception {
        // 인증이 필요한 API들에 토큰 없이 접근 시 401 응답
        String[] protectedUrls = {
                "/api/users/1",
                "/api/users/fcm-token",
                "/api/groups",
                "/api/transactions/payment",
                "/api/settlements",
                "/api/accounts",
                "/api/notifications"
        };

        for (String url : protectedUrls) {
            mockMvc.perform(get(url))
                    .andExpect(status().isUnauthorized());
        }
    }

    // 유효한 24시간 JWT 토큰으로 보호된 API 접근 테스트
    @Test
    @DisplayName("유효한 24시간 JWT 토큰으로 보호된 API 접근 성공")
    void testProtectedEndpoints_유효한24시간토큰() throws Exception {
        // Given: 유효한 24시간 JWT 토큰
        String valid24HToken = jwtProcessor.generateAccessToken("traveler@nbtrip.com", 1, "여행러");

        // When & Then: 유효한 24시간 토큰으로 접근 시 인증 통과
        mockMvc.perform(get("/api/users/1")
                        .header("Authorization", "Bearer " + valid24HToken))
                .andExpect(result -> {
                    // 401이 아니면 OK (실제 데이터는 없을 수 있어 404/400 등 가능)
                    int status = result.getResponse().getStatus();
                    if (status == 401) {
                        throw new AssertionError("24시간 토큰 인증이 실패했습니다. 상태코드: " + status);
                    }
                });
    }

    // 만료된 JWT 토큰으로 접근 테스트
    @Test
    @DisplayName("만료된 JWT 토큰으로 접근 시 401 응답")
    void testExpiredToken_접근거부() throws Exception {
        // Given: 만료된 토큰 (실제로는 Mock이나 시간 조작 필요)
        String expiredToken = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ0ZXN0QGV4YW1wbGUuY29tIiwidXNlcklkIjoxLCJuaWNrbmFtZSI6InRlc3R1c2VyIiwiZXhwIjoxfQ.invalid";

        // When & Then: 만료된 토큰으로 접근 시 401 응답
        mockMvc.perform(get("/api/users/1")
                        .header("Authorization", "Bearer " + expiredToken))
                .andExpect(status().isUnauthorized());
    }

    // 24시간 토큰 여행 그룹 시나리오 테스트
    @Test
    @DisplayName("24시간 토큰 여행 그룹 시나리오 - 연속 API 호출")
    void test24HourTokenTravelGroupScenario() throws Exception {
        // Given: 여행 그룹 사용자의 24시간 토큰
        String groupMemberToken = jwtProcessor.generateAccessToken("groupmember@nbtrip.com", 100, "그룹멤버");

        // When & Then: 24시간 동안 사용할 여행 관련 API들
        // 1. 사용자 정보 조회
        mockMvc.perform(get("/api/users/100")
                        .header("Authorization", "Bearer " + groupMemberToken))
                .andExpect(result -> {
                    assertThat(result.getResponse().getStatus()).isNotEqualTo(401);
                });

        // 2. 그룹 관련 API (향후 구현될 것들)
        mockMvc.perform(get("/api/groups")
                        .header("Authorization", "Bearer " + groupMemberToken))
                .andExpect(result -> {
                    assertThat(result.getResponse().getStatus()).isNotEqualTo(401);
                });

        // 3. 결제 관련 API (향후 구현될 것들)
        mockMvc.perform(get("/api/transactions/payment")
                        .header("Authorization", "Bearer " + groupMemberToken))
                .andExpect(result -> {
                    assertThat(result.getResponse().getStatus()).isNotEqualTo(401);
                });

        // 4. 정산 관련 API (향후 구현될 것들)
        mockMvc.perform(get("/api/settlements")
                        .header("Authorization", "Bearer " + groupMemberToken))
                .andExpect(result -> {
                    assertThat(result.getResponse().getStatus()).isNotEqualTo(401);
                });
    }

    // CORS 정책 테스트
    @Test
    @DisplayName("CORS 정책 테스트")
    void testCorsPolicy() throws Exception {
        // Given: CORS preflight 요청
        mockMvc.perform(options("/api/users/register")
                        .header("Origin", "http://localhost:3000")
                        .header("Access-Control-Request-Method", "POST")
                        .header("Access-Control-Request-Headers", "Content-Type"))
                .andExpect(status().isOk())
                .andExpect(header().exists("Access-Control-Allow-Origin"))
                .andExpect(header().exists("Access-Control-Allow-Methods"));
    }

    // 인증 실패 핸들러 테스트
    @Test
    @DisplayName("인증 실패 핸들러 동작 테스트")
    void testAuthenticationEntryPoint() throws Exception {
        // When & Then: 인증 없이 보호된 리소스 접근 시 커스텀 응답
        mockMvc.perform(get("/api/users/1"))
                .andExpect(status().isUnauthorized())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.status").value(401));
    }

    // 잘못된 토큰 형식 테스트
    @Test
    @DisplayName("잘못된 토큰 형식으로 접근 시 401 응답")
    void testInvalidTokenFormat() throws Exception {
        // Given: Bearer 없는 토큰
        String tokenWithoutBearer = "just-a-token";

        // When & Then: Bearer 형식이 아닌 토큰으로 접근 시 401 응답
        mockMvc.perform(get("/api/users/1")
                        .header("Authorization", tokenWithoutBearer))
                .andExpect(status().isUnauthorized());
    }

    // OPTIONS 메서드 테스트 (CORS preflight)
    @Test
    @DisplayName("OPTIONS 메서드는 항상 허용")
    void testOptionsMethod_허용() throws Exception {
        // When & Then: OPTIONS 메서드는 인증 없이도 허용
        mockMvc.perform(options("/api/users/1"))
                .andExpect(status().isOk());
    }
}
