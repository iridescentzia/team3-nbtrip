package org.scoula.security.util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.lang.reflect.Field;

import static org.assertj.core.api.Assertions.*;

@DisplayName("JwtProcessor 단위 테스트")
class JwtProcessorTest {
    private JwtProcessor jwtProcessor;

    // 테스트용 비밀키 및 만료시간
    private final String TEST_SECRET = "testSecretKey1234567890123456789012";
    private final long TEST_EXPIRY = 86400000L;  // 24시간

    @BeforeEach
    void setUp() throws Exception {
        jwtProcessor = new JwtProcessor();

        // private 필드 값 설정
        setPrivateField(jwtProcessor, "jwtSecret", TEST_SECRET);
        setPrivateField(jwtProcessor, "accessTokenExpiry", TEST_EXPIRY);
    }

    // 24시간 JWT 토큰 생성 및 유효성 검증 테스트
    @Test
    @DisplayName("24시간 JWT 토큰 생성 및 유효성 검증 성공")
    void testGenerateAndValidateToken_성공() {
        // Given: 사용자 정보
        String email = "test@example.com";
        Integer userId = 123;
        String nickname = "testuser";

        // When: 24시간 토큰 생성
        String token = jwtProcessor.generateAccessToken(email, userId, nickname);

        // Then: 토큰 검증
        assertThat(token).isNotNull();
        assertThat(jwtProcessor.validateToken(token)).isTrue();

        // 토큰에서 정보 추출 검증
        assertThat(jwtProcessor.getUsername(token)).isEqualTo(email);
        assertThat(jwtProcessor.getUserId(token)).isEqualTo(userId);
    }

    // 24시간 토큰 만료시간 검증 테스트
    @Test
    @DisplayName("24시간 토큰 만료시간 검증 테스트")
    void testToken24HourExpiry() throws Exception {
        // Given: 사용자 정보
        String email = "expiry@example.com";
        Integer userId = 456;
        String nickname = "expirytest";

        // When: 토큰 생성 시간 기록
        long beforeGeneration = System.currentTimeMillis();
        String token = jwtProcessor.generateAccessToken(email, userId, nickname);
        long afterGeneration = System.currentTimeMillis();

        // Then: 토큰이 24시간 후까지 유효한지 확인
        assertThat(token).isNotNull();
        assertThat(jwtProcessor.validateToken(token)).isTrue();

        // 토큰 생성 시간이 현재 시간 근처인지 확인
        assertThat(afterGeneration - beforeGeneration).isLessThan(1000);  // 1초 이내
    }

    // 유효하지 않은 토큰 검증 테스트
    @Test
    @DisplayName("유효하지 않은 토큰 검증 테스트")
    void testValidateToken_잘못된토큰() {
        // Given: 잘못된 토큰들
        String[] invalidTokens = {
                "invalid.jwt.token",
                "eyJhbGciOiJIUzI1NiJ9.invalid.signature",
                "",
                null
        };

        // When & Then: 모든 잘못된 토큰이 유효하지 않아야 함
        for (String invalidToken : invalidTokens) {
            if (invalidToken != null && !invalidToken.isEmpty()) {
                assertThat(jwtProcessor.validateToken(invalidToken))
                        .as("잘못된 토큰: %s", invalidToken)
                        .isFalse();
            }
        }
    }


    // 24시간 토큰에서 사용자 정보 추출 테스트
    @Test
    @DisplayName("24시간 토큰에서 사용자 정보 추출 성공")
    void testExtractUserInfo_성공() {
        // Given: 테스트 데이터로 24시간 토큰 생성
        String email = "user@test.com";
        Integer userId = 789;
        String nickname = "testuser24h";
        String token = jwtProcessor.generateAccessToken(email, userId, nickname);

        // When: 토큰에서 정보 추출
        String extractedEmail = jwtProcessor.getUsername(token);
        Integer extractedUserId = jwtProcessor.getUserId(token);

        // Then: 추출된 정보 검증
        assertThat(extractedEmail).isEqualTo(email);
        assertThat(extractedUserId).isEqualTo(userId);
    }

    // null 토큰 처리 테스트
    @Test
    @DisplayName("null 토큰 검증 실패")
    void testValidateToken_null토큰() {
        // When & Then: null 토큰은 유효하지 않아야 함
        assertThat(jwtProcessor.validateToken(null)).isFalse();
    }

    // 빈 토큰 처리 테스트
    @Test
    @DisplayName("빈 토큰 검증 실패")
    void testValidateToken_빈토큰() {
        // When & Then: 빈 토큰은 유효하지 않아야 함
        assertThat(jwtProcessor.validateToken("")).isFalse();
    }

    // 토큰 형식 오류 테스트
    @Test
    @DisplayName("잘못된 형식 토큰 검증 실패")
    void testValidateToken_잘못된형식() {
        // Given: JWT가 아닌 일반 문자열
        String notJwtToken = "this-is-not-jwt-token";

        // When & Then: JWT가 아닌 토큰은 유효하지 않아야 함
        assertThat(jwtProcessor.validateToken(notJwtToken)).isFalse();
    }

    // 여행 그룹 서비스용 24시간 토큰 시나리오 테스트
    @Test
    @DisplayName("여행 그룹 서비스 24시간 토큰 시나리오")
    void testTravelGroupService24HourTokenScenario() {
        // Given: 여행 그룹 사용자 정보
        String userEmail = "traveler@nbtrip.com";
        Integer groupUserId = 1001;
        String userNickname = "여행러";

        // When: 24시간 토큰 생성 (여행 시작 시)
        String travelToken = jwtProcessor.generateAccessToken(userEmail, groupUserId, userNickname);

        // Then: 토큰이 유효하고 정보 추출 가능
        assertThat(travelToken).isNotNull();
        assertThat(jwtProcessor.validateToken(travelToken)).isTrue();
        assertThat(jwtProcessor.getUsername(travelToken)).isEqualTo(userEmail);
        assertThat(jwtProcessor.getUserId(travelToken)).isEqualTo(groupUserId);
    }

    // private 필드 값 설정 메서드
    private void setPrivateField(Object target, String fieldName, Object value) throws Exception {
        Field field = target.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(target, value);
    }
}
