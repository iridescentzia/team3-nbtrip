package org.scoula.member.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.scoula.member.domain.MemberVO;
import org.scoula.member.dto.*;
import org.scoula.member.exception.*;
import org.scoula.member.mapper.MemberMapper;
import org.scoula.security.util.JwtProcessor;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("MemberService 단위 테스트(24시간 토큰)")
class MemberServiceTest {
    @Mock private MemberMapper memberMapper;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private JwtProcessor jwtProcessor;
    @InjectMocks private MemberServiceImpl memberService;

    private MemberDTO validMemberDTO;
    private MemberVO existingMember;
    private MemberLoginRequestDTO loginRequest;
    private static final long TOKEN_24_HOURS = 86400000L;  // 만료 시간(24시간)

    @BeforeEach
    void setUp() {
        // 테스트용 회원가입 데이터
        validMemberDTO = new MemberDTO();
        validMemberDTO.setEmail("test@example.com");
        validMemberDTO.setPassword("Test1234!");
        validMemberDTO.setPasswordConfirm("Test1234!");
        validMemberDTO.setNickname("testuser");
        validMemberDTO.setName("테스트유저");
        validMemberDTO.setPhoneNumber("010-1234-5678");
        validMemberDTO.setFcmToken("fcm-token-123");

        // 기존 회원 데이터
        existingMember = MemberVO.builder()
                .userId(1)
                .email("test@example.com")
                .password("encodedPassword")
                .nickname("testuser")
                .name("테스트유저")
                .phoneNumber("010-1234-5678")
                .createdAt(LocalDateTime.now())
                .build();

        // 로그인 요청 데이터
        loginRequest = new MemberLoginRequestDTO();
        loginRequest.setEmail("test@example.com");
        loginRequest.setPassword("Test1234!");
    }

    // 회원가입 성공 테스트
    @Test
    @DisplayName("회원가입 성공 테스트")
    void testRegisterMember_성공() {
        // Given: 중복 없는 상황 설정
        when(memberMapper.existsByEmail(anyString())).thenReturn(false);
        when(memberMapper.existsByNickname(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");

        // When: 회원가입 실행
        ApiResponse result = memberService.registerMember(validMemberDTO);

        // Then: 결과 검증
        assertThat(result.isSuccess()).isTrue();
        assertThat(result.getMessage()).isEqualTo("회원가입이 완료되었습니다.");

        // Mock 호출 검증
        verify(memberMapper, times(1)).existsByEmail("test@example.com");
        verify(memberMapper, times(1)).existsByNickname("testuser");
        verify(passwordEncoder, times(1)).encode("Test1234!");
        verify(memberMapper, times(1)).insertMember(any(MemberVO.class));
    }

    // 회원가입 - 이메일 중복 테스트
    @Test
    @DisplayName("회원가입 - 이메일 중복 시 예외 발생")
    void testRegisterMember_이메일중복() {
        // Given: 이메일 중복 상황
        when(memberMapper.existsByEmail(anyString())).thenReturn(true);

        // When & Then: 예외 발생 확인
        assertThatThrownBy(() -> memberService.registerMember(validMemberDTO))
                .isInstanceOf(DuplicateEmailException.class)
                .hasMessage("이미 가입된 이메일입니다.");
    }

    // 회원가입 - 닉네임 중복 테스트
    @Test
    @DisplayName("회원가입 - 닉네임 중복 시 예외 발생")
    void testRegisterMember_닉네임중복() {
        // Given: 닉네임 중복 상황
        when(memberMapper.existsByEmail(anyString())).thenReturn(false);
        when(memberMapper.existsByNickname(anyString())).thenReturn(true);

        // When & Then: 예외 발생 확인
        assertThatThrownBy(() -> memberService.registerMember(validMemberDTO))
                .isInstanceOf(DuplicateNicknameException.class)
                .hasMessage("이미 사용중인 닉네임입니다.");
    }

    // 로그인 성공 테스트 - 24시간 토큰 발급 확인
    @Test
    @DisplayName("로그인 성공 - 24시간 유효 토큰 발급")
    void testLoginMember_성공() {
        // Given: 로그인 성공 상황 설정
        when(memberMapper.findByEmail("test@example.com")).thenReturn(Optional.of(existingMember));
        when(passwordEncoder.matches("Test1234!", "encodedPassword")).thenReturn(true);
        when(jwtProcessor.generateAccessToken(anyString(), anyInt(), anyString())).thenReturn("jwt-token");

        // When: 로그인 실행
        MemberLoginResponseDTO result = memberService.loginMember(loginRequest);

        // Then: 결과 검증
        assertThat(result).isNotNull();
        assertThat(result.getAccessToken()).isEqualTo("jwt-token");
        assertThat(result.getMember().getUserId()).isEqualTo(1);
        assertThat(result.getExpiresIn()).isEqualTo(TOKEN_24_HOURS);
        verify(memberMapper, times(1)).findByEmail("test@example.com");
        verify(passwordEncoder, times(1)).matches("Test1234!", "encodedPassword");
    }

    // 로그인 실패 - 존재하지 않는 회원
    @Test
    @DisplayName("로그인 실패 - 존재하지 않는 회원")
    void testLoginMember_존재하지않는회원() {
        // Given: 회원이 존재하지 않는 상황
        when(memberMapper.findByEmail(anyString())).thenReturn(Optional.empty());

        // When & Then: 예외 발생 확인
        assertThatThrownBy(() -> memberService.loginMember(loginRequest))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessage("존재하지 않는 회원입니다.");
    }

    // 로그인 실패 - 비밀번호 불일치
    @Test
    @DisplayName("로그인 실패 - 비밀번호 불일치")
    void testLoginMember_비밀번호불일치() {
        // Given: 비밀번호 불일치 상황
        when(memberMapper.findByEmail("test@example.com")).thenReturn(Optional.of(existingMember));
        when(passwordEncoder.matches("Test1234!", "encodedPassword")).thenReturn(false);

        // When & Then: 예외 발생 확인
        assertThatThrownBy(() -> memberService.loginMember(loginRequest))
                .isInstanceOf(AuthenticationException.class)
                .hasMessage("이메일 또는 비밀번호가 올바르지 않습니다.");
    }

    // 회원 정보 조회 성공 테스트
    @Test
    @DisplayName("회원 정보 조회 성공 테스트")
    void testGetMemberInfo_성공() {
        // Given: 회원 존재 상황
        when(memberMapper.findById(1)).thenReturn(Optional.of(existingMember));

        // When: 회원 정보 조회
        MemberResponseDTO result = memberService.getMemberInfo(1);

        // Then: 결과 검증
        assertThat(result).isNotNull();
        assertThat(result.getUserId()).isEqualTo(1);
        assertThat(result.getEmail()).isEqualTo("test@example.com");
        assertThat(result.getNickname()).isEqualTo("testuser");

        verify(memberMapper, times(1)).findById(1);
    }

    // FCM 토큰 갱신 성공 테스트
    @Test
    @DisplayName("FCM 토큰 갱신 성공 테스트")
    void testUpdateFcmToken_성공() {
        // Given: 회원 존재 및 FCM 토큰 DTO
        when(memberMapper.existsById(1)).thenReturn(true);
        MemberFcmTokenDTO fcmTokenDTO = new MemberFcmTokenDTO();
        fcmTokenDTO.setFcmToken("new-fcm-token");

        // When: FCM 토큰 갱신
        memberService.updateFcmToken(1, fcmTokenDTO);

        // Then: Mock 호출 검증
        verify(memberMapper, times(1)).existsById(1);
        verify(memberMapper, times(1)).updateFcmToken(1, "new-fcm-token");
    }

    // 닉네임 중복 체크 테스트
    @Test
    @DisplayName("닉네임 중복 체크 테스트")
    void testCheckNicknameDuplicate() {
        // Given: 닉네임 중복 상황
        when(memberMapper.existsByNickname("existing")).thenReturn(true);
        when(memberMapper.existsByNickname("available")).thenReturn(false);

        // When & Then: 중복 체크 결과 확인
        assertThat(memberService.checkNicknameDuplicate("existing")).isTrue();
        assertThat(memberService.checkNicknameDuplicate("available")).isFalse();
    }

    // 로그아웃 성공 테스트
    @Test
    @DisplayName("로그아웃 성공 - FCM 토큰 삭제")
    void testLogoutMember_성공() {
        // Given: 회원 존재 상황
        when(memberMapper.existsById(1)).thenReturn(true);

        // When: 로그아웃 실행
        memberService.logoutMember(1);

        // Then: FCM 토큰 삭제 확인
        verify(memberMapper, times(1)).existsById(1);
        verify(memberMapper, times(1)).updateFcmToken(1, null);
    }
}
