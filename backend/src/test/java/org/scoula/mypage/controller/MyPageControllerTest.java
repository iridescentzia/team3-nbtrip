package org.scoula.mypage.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import org.scoula.mypage.dto.UserUpdateRequestDTO;
import org.scoula.mypage.dto.PasswordChangeRequestDTO;
import org.scoula.mypage.dto.FcmTokenRequestDTO;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
        "classpath:spring/root-context.xml",
        "classpath:spring/security-context.xml",
        "classpath:spring/servlet-context.xml"
})
@WebAppConfiguration
@Transactional
public class MyPageControllerTest {

    @Autowired
    private WebApplicationContext wac;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @Before
    public void setUp() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(wac)
                .apply(springSecurity())
                .build();
        objectMapper = new ObjectMapper();
    }

    // 사용자 정보 조회 테스트(성공) - GET /api/users/{userId}
    @Test
    @WithMockUser(username = "1", roles = "USER")
    public void testGetUserInfo_Success() throws Exception {
        // Given
        Integer userId = 1;

        // When & Then
        mockMvc.perform(get("/api/users/{userId}", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").exists())
                .andExpect(jsonPath("$.data.userId").value(userId));
    }

    // 사용자 정보 조회 테스트(권한 없음)
    @Test
    @WithMockUser(username = "1", roles = "USER")
    public void testGetUserInfo_Forbidden() throws Exception {
        // Given
        Integer userId = 2; // 다른 사용자 ID

        // When & Then
        mockMvc.perform(get("/api/users/{userId}", userId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isForbidden())  // 🔧 fixed typo
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("본인의 정보만 조회할 수 있습니다."));
    }

    // 사용자 정보 수정 테스트(성공) - PUT /api/users/{userId}
    @Test
    @WithMockUser(username = "1", roles = "USER")
    public void testUpdateUserInfo_Success() throws Exception {
        // Given
        Integer userId = 1;
        UserUpdateRequestDTO requestDTO = new UserUpdateRequestDTO();
        requestDTO.setEmail("updated@example.com");
        requestDTO.setNickname("updateduser");
        requestDTO.setName("수정된유저");
        requestDTO.setPhoneNumber("010-9876-5432");

        // When & Then
        mockMvc.perform(put("/api/users/{userId}", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").exists());
    }

    // 비밀번호 변경 테스트(성공) - PUT /api/users/{userId}/password
    @Test
    @WithMockUser(username = "1", roles = "USER")
    public void testChangePassword_Success() throws Exception {
        // Given
        Integer userId = 1;
        PasswordChangeRequestDTO requestDTO = new PasswordChangeRequestDTO();
        requestDTO.setCurrentPassword("oldPassword123!");
        requestDTO.setNewPassword("newPassword123!");
        requestDTO.setNewPasswordConfirm("newPassword123!");

        // When & Then
        mockMvc.perform(put("/api/users/{userId}/password", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").exists());
    }

    // FCM 토큰 갱신 테스트(성공) - PUT /api/users/fcm-token
    @Test
    @WithMockUser(username = "1", roles = "USER")
    public void testUpdateFcmToken_Success() throws Exception {
        // Given
        FcmTokenRequestDTO requestDTO = new FcmTokenRequestDTO();
        requestDTO.setFcmToken("new_fcm_token_12345");

        // When & Then
        mockMvc.perform(put("/api/users/fcm-token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").exists());
    }

    // 인증되지 않은 사용자 접근 테스트
    @Test
    public void testGetUserInfo_Unauthorized() throws Exception {
        // Given
        Integer userId = 1;

        // When & Then
        mockMvc.perform(get("/api/users/{userId}", userId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }
}
