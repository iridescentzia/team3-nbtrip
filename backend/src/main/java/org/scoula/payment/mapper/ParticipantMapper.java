package org.scoula.payment.mapper;

import org.apache.ibatis.annotations.Param;
import org.scoula.payment.domain.ParticipantVO;

import java.util.List;

public interface ParticipantMapper {

    // 특정 paymentId에 대한 모든 참여자 목록 조회
    List<ParticipantVO> findByPaymentId(int paymentId);

    // 결제 참여자 단건 등록
    int insertParticipant(ParticipantVO participant);

    // 결제 참여자 여러 명 등록(배치 처리)
    int insertParticipants(@Param("participants") List<ParticipantVO> participants);

    // 결제 참여자 분배 금액 수정
    int updateAmount(@Param("paymentId") int paymentId, @Param("userId") int userId, @Param("splitAmount") int splitAmount);

    // 결제 참여자 삭제 (paymentId 기준)
    int deleteParticipant(@Param("paymentId") int paymentId, @Param("userId") int userId);
}
