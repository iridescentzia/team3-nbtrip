package org.scoula.security.accounting.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.scoula.security.accounting.domain.MemberVO;

@Mapper
public interface UserDetailsMapper {
    MemberVO getByEmail(String email);  // 사용자 조회(email - 로그인)
    MemberVO getByNickname(String nickname);  // 사용자 조회(nickname)
    MemberVO getById(int userId);  // 사용자 조회(userId)
    void updateFcmToken(@Param("email") String email, @Param("fcmToken") String fcmToken);  // FCM 토큰 업데이트
}