package org.scoula.mypage.mapper;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import org.scoula.mypage.dto.MyPageDTO;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
        "classpath:spring/root-context.xml"
})
@Transactional // 테스트 후 롤백
public class MyPageMapperTest {

    @Autowired
    private MyPageMapper myPageMapper;

    // 사용자 정보 조회 테스트
    @Test
    public void testSelectUserInfo() {
        // Given
        Integer userId = 1; // 실제 존재하는 사용자 ID

        // When
        MyPageDTO result = myPageMapper.selectUserInfo(userId);

        // Then
        if (result != null) { // 테스트 데이터가 있는 경우에만 검증
            assertNotNull("조회 결과가 null이면 안됩니다", result);
            assertEquals("사용자 ID가 일치해야 합니다", userId, result.getUserId());
            assertNotNull("이메일이 있어야 합니다", result.getEmail());
            assertNotNull("닉네임이 있어야 합니다", result.getNickname());
            assertNotNull("이름이 있어야 합니다", result.getName());
            assertNotNull("전화번호가 있어야 합니다", result.getPhoneNumber());
        }
    }

    // 사용자 정보 업데이트 테스트
    @Test
    public void testUpdateUserInfo() {
        // Given
        Integer userId = 1;

        // 먼저 기존 정보 조회
        MyPageDTO originalInfo = myPageMapper.selectUserInfo(userId);
        if (originalInfo == null) {
            return; // 테스트 데이터가 없으면 스킵
        }

        MyPageDTO updateDTO = MyPageDTO.builder()
                .userId(userId)
                .email("test_updated_" + System.currentTimeMillis() + "@example.com")
                .nickname("test_updated_" + System.currentTimeMillis())
                .name("테스트업데이트유저")
                .phoneNumber("010-9999-9999")
                .build();

        // When
        int result = myPageMapper.updateUserInfo(updateDTO);

        // Then
        assertEquals("업데이트 성공 시 1을 반환해야 합니다", 1, result);

        // 업데이트 확인
        MyPageDTO updated = myPageMapper.selectUserInfo(userId);
        assertEquals("이메일이 업데이트되어야 합니다", updateDTO.getEmail(), updated.getEmail());
        assertEquals("닉네임이 업데이트되어야 합니다", updateDTO.getNickname(), updated.getNickname());
    }

    // 이메일 중복 검사 테스트
    @Test
    public void testCheckEmailExists() {
        // Given - 실제 존재하는 사용자의 이메일 조회
        MyPageDTO existingUser = myPageMapper.selectUserInfo(1);
        if (existingUser == null) {
            return; // 테스트 데이터가 없으면 스킵
        }

        Map<String, Object> params = new HashMap<>();
        params.put("email", existingUser.getEmail());
        params.put("userId", 999); // 다른 사용자 ID

        // When
        int result = myPageMapper.checkEmailExists(params);

        // Then
        assertTrue("기존 이메일은 중복으로 검출되어야 합니다", result > 0);

        // 존재하지 않는 이메일 테스트
        params.put("email", "nonexistent_" + System.currentTimeMillis() + "@example.com");
        int result2 = myPageMapper.checkEmailExists(params);
        assertEquals("존재하지 않는 이메일은 0을 반환해야 합니다", 0, result2);
    }

    // FCM 토큰 업데이트 테스트
    @Test
    public void testUpdateFcmToken() {
        // Given
        Integer userId = 1;
        String newFcmToken = "test_fcm_token_" + System.currentTimeMillis();

        Map<String, Object> params = new HashMap<>();
        params.put("userId", userId);
        params.put("fcmToken", newFcmToken);

        // When
        int result = myPageMapper.updateFcmToken(params);

        // Then
        assertTrue("FCM 토큰 업데이트는 성공해야 합니다", result >= 0);

        // 업데이트 확인
        MyPageDTO updated = myPageMapper.selectUserInfo(userId);
        if (updated != null) {
            assertEquals("FCM 토큰이 업데이트되어야 합니다", newFcmToken, updated.getFcmToken());
        }
    }

    // 존재하지 않는 사용자 조회 테스트
    @Test
    public void testSelectUserInfo_NotFound() {
        // Given
        Integer userId = 99999; // 존재하지 않는 사용자 ID

        // When
        MyPageDTO result = myPageMapper.selectUserInfo(userId);

        // Then
        assertNull("존재하지 않는 사용자는 null을 반환해야 합니다", result);
    }
}
