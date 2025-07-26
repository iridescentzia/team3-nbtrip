package org.scoula.security.util;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.assertj.core.api.Assertions.*;

@DisplayName("CryptoUtil 단위 테스트")
class CryptoUtilTest {

    // 문자열 암호화/복호화 테스트
    @Test
    @DisplayName("문자열 암호화/복호화 성공")
    void testEncryptDecrypt_성공() throws Exception {
        // Given: 원본 데이터
        String originalText = "010-1234-5678";

        // When: 암호화 후 복호화
        String encrypted = CryptoUtil.encrypt(originalText);
        String decrypted = CryptoUtil.decrypt(encrypted);

        // Then: 검증
        assertThat(encrypted).isNotNull();
        assertThat(encrypted).isNotEqualTo(originalText);
        assertThat(decrypted).isEqualTo(originalText);
    }

    // 빈 문자열 암호화 테스트
    @Test
    @DisplayName("빈 문자열 암호화/복호화 성공")
    void testEncryptEmpty_성공() throws Exception {
        // Given: 빈 문자열
        String emptyText = "";

        // When: 암호화 후 복호화
        String encrypted = CryptoUtil.encrypt(emptyText);
        String decrypted = CryptoUtil.decrypt(encrypted);

        // Then: 빈 문자열도 정상 처리
        assertThat(decrypted).isEqualTo(emptyText);
    }

    // 한글 텍스트 암호화 테스트
    @Test
    @DisplayName("한글 텍스트 암호화/복호화 성공")
    void testEncryptKorean_성공() throws Exception {
        // Given: 한글 텍스트
        String koreanText = "테스트 사용자";

        // When: 암호화 후 복호화
        String encrypted = CryptoUtil.encrypt(koreanText);
        String decrypted = CryptoUtil.decrypt(encrypted);

        // Then: 한글도 정상 처리
        assertThat(decrypted).isEqualTo(koreanText);
    }

    // 여행 그룹용 개인정보 암호화 시나리오 테스트
    @Test
    @DisplayName("여행 그룹 개인정보 암호화 시나리오")
    void testTravelGroupPrivacyEncryption() throws Exception {
        // Given: 여행 그룹 회원의 개인정보
        String phoneNumber = "010-9876-5432";
        String accountNumber = "110-123-456789";
        String realName = "김여행";

        // When: 개인정보 암호화
        String encryptedPhone = CryptoUtil.encrypt(phoneNumber);
        String encryptedAccount = CryptoUtil.encrypt(accountNumber);
        String encryptedName = CryptoUtil.encrypt(realName);

        // Then: 암호화 성공 및 복호화 일치 확인
        assertThat(CryptoUtil.decrypt(encryptedPhone)).isEqualTo(phoneNumber);
        assertThat(CryptoUtil.decrypt(encryptedAccount)).isEqualTo(accountNumber);
        assertThat(CryptoUtil.decrypt(encryptedName)).isEqualTo(realName);

        // 암호화된 데이터는 원본과 달라야 함
        assertThat(encryptedPhone).isNotEqualTo(phoneNumber);
        assertThat(encryptedAccount).isNotEqualTo(accountNumber);
        assertThat(encryptedName).isNotEqualTo(realName);
    }

    // null 값 처리 테스트
    @Test
    @DisplayName("null 값 암호화 시 예외 발생")
    void testEncryptNull_예외발생() {
        // When & Then: null 값 암호화 시 예외 발생
        assertThatThrownBy(() -> CryptoUtil.encrypt(null))
                .isInstanceOf(Exception.class);
    }

    // null 값 복호화 테스트
    @Test
    @DisplayName("null 값 복호화 시 예외 발생")
    void testDecryptNull_예외발생() {
        // When & Then: null 값 복호화 시 예외 발생
        assertThatThrownBy(() -> CryptoUtil.decrypt(null))
                .isInstanceOf(Exception.class);
    }
}
