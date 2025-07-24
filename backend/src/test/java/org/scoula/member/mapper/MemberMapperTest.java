package org.scoula.member.mapper;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.transaction.annotation.Transactional;
import org.scoula.member.domain.MemberVO;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

@SpringJUnitConfig(locations = {
        "classpath:spring/root-context.xml",
        "classpath:spring/test-context.xml"
})
@Transactional
@DisplayName("MemberMapper 매퍼 테스트")
class MemberMapperTest {
    @Autowired
    private MemberMapper memberMapper;

    // 여행 그룹 회원 정보 저장 및 조회 테스트
    @Test
    @DisplayName("여행 그룹 회원 정보 저장 및 조회 테스트")
    void testInsertAndFindTravelGroupMember() {
        // Given: 여행 그룹 회원 데이터
        MemberVO travelMember = MemberVO.builder()
                .email("traveler@nbtrip.com")
                .password("encodedTravelPassword")
                .nickname("여행러")
                .name("김여행")
                .phoneNumber("010-1234-5678")
                .fcmToken("travel-fcm-token")
                .createdAt(LocalDateTime.now())
                .build();

        // When: 회원 정보 저장
        memberMapper.insertMember(travelMember);

        // 자동 생성된 userId 확인
        assertThat(travelMember.getUserId()).isNotNull();
        assertThat(travelMember.getUserId()).isGreaterThan(0);

        // Then: 저장된 회원 조회
        Optional<MemberVO> found = memberMapper.findByEmail("traveler@nbtrip.com");
        assertThat(found).isPresent();

        MemberVO foundMember = found.get();
        assertThat(foundMember.getEmail()).isEqualTo("traveler@nbtrip.com");
        assertThat(foundMember.getNickname()).isEqualTo("여행러");
        assertThat(foundMember.getName()).isEqualTo("김여행");
    }

    // 이메일 존재 여부 확인 테스트
    @Test
    @DisplayName("이메일 존재 여부 확인 테스트")
    void testExistsByEmail() {
        // Given: 테스트 회원 생성
        MemberVO member = MemberVO.builder()
                .email("exists-test@nbtrip.com")
                .password("password")
                .nickname("존재테스트")
                .name("존재테스트")
                .phoneNumber("010-1111-1111")
                .createdAt(LocalDateTime.now())
                .build();
        memberMapper.insertMember(member);

        // When & Then: 존재 여부 확인
        assertThat(memberMapper.existsByEmail("exists-test@nbtrip.com")).isTrue();
        assertThat(memberMapper.existsByEmail("not-exists@nbtrip.com")).isFalse();
    }

    // 닉네임 존재 여부 확인 테스트
    @Test
    @DisplayName("닉네임 존재 여부 확인 테스트")
    void testExistsByNickname() {
        // Given: 테스트 회원 생성
        MemberVO member = MemberVO.builder()
                .email("nickname-test@nbtrip.com")
                .password("password")
                .nickname("유니크닉네임")
                .name("닉네임테스트")
                .phoneNumber("010-2222-2222")
                .createdAt(LocalDateTime.now())
                .build();
        memberMapper.insertMember(member);

        // When & Then: 닉네임 존재 여부 확인
        assertThat(memberMapper.existsByNickname("유니크닉네임")).isTrue();
        assertThat(memberMapper.existsByNickname("존재하지않는닉네임")).isFalse();
    }

    // 회원 정보 수정 테스트
    @Test
    @DisplayName("회원 정보 수정 테스트")
    void testUpdateMember() {
        // Given: 테스트 회원 생성
        MemberVO member = MemberVO.builder()
                .email("update-test@nbtrip.com")
                .password("password")
                .nickname("수정전닉네임")
                .name("수정전이름")
                .phoneNumber("010-3333-3333")
                .createdAt(LocalDateTime.now())
                .build();
        memberMapper.insertMember(member);

        // When: 회원 정보 수정
        member.setNickname("수정후닉네임");
        member.setName("수정후이름");
        member.setPhoneNumber("010-4444-4444");
        memberMapper.updateMember(member);

        // Then: 수정된 정보 확인
        Optional<MemberVO> updated = memberMapper.findById(member.getUserId());
        assertThat(updated).isPresent();

        MemberVO updatedMember = updated.get();
        assertThat(updatedMember.getNickname()).isEqualTo("수정후닉네임");
        assertThat(updatedMember.getName()).isEqualTo("수정후이름");
        assertThat(updatedMember.getPhoneNumber()).isEqualTo("010-4444-4444");
    }

    // FCM 토큰 업데이트 테스트
    @Test
    @DisplayName("FCM 토큰 업데이트 테스트")
    void testUpdateFcmToken() {
        // Given: 테스트 회원 생성
        MemberVO member = MemberVO.builder()
                .email("fcm-test@nbtrip.com")
                .password("password")
                .nickname("FCM테스터")
                .name("FCM테스트")
                .phoneNumber("010-5555-5555")
                .fcmToken("old-fcm-token")
                .createdAt(LocalDateTime.now())
                .build();
        memberMapper.insertMember(member);

        // When: FCM 토큰 업데이트
        memberMapper.updateFcmToken(member.getUserId(), "new-travel-fcm-token");

        // Then: 업데이트된 토큰 확인
        Optional<MemberVO> updated = memberMapper.findById(member.getUserId());
        assertThat(updated).isPresent();
        assertThat(updated.get().getFcmToken()).isEqualTo("new-travel-fcm-token");
    }

    // 닉네임으로 사용자 ID 조회 테스트
    @Test
    @DisplayName("닉네임으로 사용자 ID 조회 테스트")
    void testFindUserIdByNickname() {
        // Given: 테스트 회원 생성
        MemberVO member = MemberVO.builder()
                .email("find-by-nick@nbtrip.com")
                .password("password")
                .nickname("찾을닉네임")
                .name("닉네임찾기")
                .phoneNumber("010-6666-6666")
                .createdAt(LocalDateTime.now())
                .build();
        memberMapper.insertMember(member);

        // When: 닉네임으로 사용자 ID 조회
        int foundUserId = memberMapper.findUserIdByNickname("찾을닉네임");

        // Then: 조회된 사용자 ID 확인
        assertThat(foundUserId).isEqualTo(member.getUserId());

        // 존재하지 않는 닉네임의 경우
        int notFoundUserId = memberMapper.findUserIdByNickname("없는닉네임");
        assertThat(notFoundUserId).isEqualTo(0);
    }

    // 사용자 ID로 회원 존재 여부 확인 테스트
    @Test
    @DisplayName("사용자 ID로 회원 존재 여부 확인 테스트")
    void testExistsById() {
        // Given: 테스트 회원 생성
        MemberVO member = MemberVO.builder()
                .email("exists-by-id@nbtrip.com")
                .password("password")
                .nickname("ID존재테스트")
                .name("ID존재테스트")
                .phoneNumber("010-7777-7777")
                .createdAt(LocalDateTime.now())
                .build();
        memberMapper.insertMember(member);

        // When & Then: ID로 존재 여부 확인
        assertThat(memberMapper.existsById(member.getUserId())).isTrue();
        assertThat(memberMapper.existsById(99999)).isFalse(); // 존재하지 않는 ID
    }

    // 비밀번호 업데이트 테스트
    @Test
    @DisplayName("비밀번호 업데이트 테스트")
    void testUpdatePassword() {
        // Given: 테스트 회원 생성
        MemberVO member = MemberVO.builder()
                .email("password-test@nbtrip.com")
                .password("oldEncodedPassword")
                .nickname("비밀번호테스터")
                .name("비밀번호테스트")
                .phoneNumber("010-8888-8888")
                .createdAt(LocalDateTime.now())
                .build();
        memberMapper.insertMember(member);

        // When: 비밀번호 업데이트
        String newEncodedPassword = "newEncodedTravelPassword";
        memberMapper.updatePassword(member.getUserId(), newEncodedPassword);

        // Then: 업데이트된 비밀번호 확인
        Optional<MemberVO> updated = memberMapper.findById(member.getUserId());
        assertThat(updated).isPresent();
        assertThat(updated.get().getPassword()).isEqualTo(newEncodedPassword);
    }
}
