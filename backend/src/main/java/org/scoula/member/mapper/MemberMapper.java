package org.scoula.member.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.scoula.member.domain.MemberVO;

import java.util.List;
import java.util.Optional;

@Mapper
public interface MemberMapper {
    // 회원 정보 저장
    void insertMember(MemberVO memberVO);

    // email로 회원 조회
    MemberVO findByEmail(@Param("email") String email);

    // userId로 회원 조회
    MemberVO findById(@Param("userId") int userId);

    // nickname으로 userId 조회
    int findUserIdByNickname(@Param("nickname") String nickname);

    // email 존재 여부 확인
    boolean existsByEmail(@Param("email") String email);

    // nickname 존재 여부 확인
    boolean existsByNickname(@Param("nickname") String nickname);

    // nickname으로 user 검색
    List<MemberVO> searchUserByNickname(@Param("nickname") String nickname);

    // userId 존재 여부 확인
    boolean existsById(@Param("userId") int userId);

    // 회원 정보 수정
    void updateMember(MemberVO memberVO);

    // 비밀번호 변경
    void updatePassword(@Param("userId") int userId, @Param("password") String encodedPassword);

    // FCM 토큰 업데이트
    void updateFcmToken(@Param("userId") int userId, @Param("fcmToken") String fcmToken);
}
