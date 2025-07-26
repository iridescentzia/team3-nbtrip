package org.scoula.mypage.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import org.scoula.mypage.dto.MyPageDTO;
import org.scoula.mypage.dto.UserUpdateRequestDTO;
import org.scoula.mypage.dto.PasswordChangeRequestDTO;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
        "classpath:spring/root-context.xml",
        "classpath:spring/security-context.xml"
})
@Transactional
public class MyPageServiceTest {

    @Autowired
    private MyPageService myPageService;

    // 사용자 정보 조회 테스트 - 성공 케이스
    @Test
    public void testGetUserInfo_Success() throws Exception {
        // Given
        Integer userId = 1; // 실제 테스트 데이터가 있다고 가정

        // When
        MyPageDTO result = myPageService.getUserInfo(userId);

        // Then
        assertNotNull("사용자 정보가 조회되어야 합니다", result);
        assertEquals("사용자 ID가 일치해야 합니다", userId, result.getUserId());
        assertNotNull("이메일이 있어야 합니다", result.getEmail());
        assertNotNull("닉네임이 있어야 합니다", result.getNickname());
        assertNotNull("이름이 있어야 합니다", result.getName());
        assertNotNull("전화번호가 있어야 합니다", result.getPhoneNumber());

        // 여행 그룹 관리 시스템 특성: 개인정보 보호
        String maskedPhone = result.getMaskedPhoneNumber();
        assertTrue("휴대폰 번호가 마스킹되어야 합니다", maskedPhone.contains("****"));
    }

    // 사용자 정보 조회 테스트 - 존재하지 않는 사용자
    @Test(expected = Exception.class)
    public void testGetUserInfo_UserNotFound() throws Exception {
        // Given
        Integer userId = 99999; // 존재하지 않는 사용자 ID

        // When
        myPageService.getUserInfo(userId);

        // Then - Exception 발생 예상
    }

    // 사용자 정보 업데이트 테스트 - 성공 케이스
    @Test
    public void testUpdateUserInfo_Success() throws Exception {
        // Given
        Integer userId = 1;
        UserUpdateRequestDTO requestDTO = new UserUpdateRequestDTO();
        requestDTO.setEmail("updated" + System.currentTimeMillis() + "@example.com"); // 유니크한 이메일
        requestDTO.setNickname("updated" + System.currentTimeMillis()); // 유니크한 닉네임
        requestDTO.setName("수정된유저");
        requestDTO.setPhoneNumber("010-9999-8888");

        // When
        boolean result = myPageService.updateUserInfo(userId, requestDTO);

        // Then
        assertTrue("사용자 정보 업데이트가 성공해야 합니다", result);

        // 업데이트 확인
        MyPageDTO updated = myPageService.getUserInfo(userId);
        assertEquals("이메일이 업데이트되어야 합니다", requestDTO.getEmail(), updated.getEmail());
        assertEquals("닉네임이 업데이트되어야 합니다", requestDTO.getNickname(), updated.getNickname());
    }

    // FCM 토큰 갱신 테스트
    @Test
    public void testUpdateFcmToken_Success() throws Exception {
        // Given
        Integer userId = 1;
        String fcmToken = "test_fcm_token_" + System.currentTimeMillis();

        // When
        boolean result = myPageService.updateFcmToken(userId, fcmToken);

        // Then
        assertTrue("FCM 토큰 갱신이 성공해야 합니다", result);

        // 갱신 확인
        MyPageDTO updated = myPageService.getUserInfo(userId);
        assertEquals("FCM 토큰이 업데이트되어야 합니다", fcmToken, updated.getFcmToken());
    }

    // 유효하지 않은 사용자 ID 테스트
    @Test(expected = IllegalArgumentException.class)
    public void testGetUserInfo_InvalidUserId() throws Exception {
        // Given
        Integer invalidUserId = null;

        // When
        myPageService.getUserInfo(invalidUserId);

        // Then - IllegalArgumentException 발생 예상
    }
}
