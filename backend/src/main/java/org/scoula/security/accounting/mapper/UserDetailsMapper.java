package org.scoula.security.accounting.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.scoula.security.accounting.domain.MemberVO;

@Mapper
public interface UserDetailsMapper {
    // 사용자 조회(email - 로그인)
    MemberVO getByEmail(String email);

    // 사용자 조회(nickname)
    MemberVO getByNickname(String nickname);

    // 사용자 조회(userId)
    MemberVO getById(int userId);

    // FCM 토큰 업데이트
    void updateFcmToken(@Param("email") String email, @Param("fcmToken") String fcmToken);
}