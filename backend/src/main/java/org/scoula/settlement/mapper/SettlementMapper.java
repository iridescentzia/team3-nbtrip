package org.scoula.settlement.mapper;

import org.apache.ibatis.annotations.Param;
import org.scoula.settlement.domain.SettlementVO;

import java.util.List;

// mybatis 정산 매퍼, SettlementMapper.xml과 연동하여 sql 실행
public interface SettlementMapper {
    // 특정 유저가 참여한 모든 정산 내역 조회
    public List<SettlementVO> getSettlementsByUserId(@Param("userId") int userId);

    // 특정 여행의 정산 내역 조회
    public List<SettlementVO> getSettlementsByTripId(@Param("tripId") int tripId);

    // 상태별 정산 내역 필터링 조회 : pending, processing, completed
    public List<SettlementVO> getSettlementsByStatus(@Param("status") String status);

    // 정산 상태 업데이트 : pending -> processing
    public int updateSettlementStatus(@Param("settlementId") int settlementId, @Param("newStatus") String newStatus);

    // 정산 ID로 단건 조회 : 상태 확인 또는 상세 조회용
    public SettlementVO getById(@Param("settlementId") int settlementId);

    // 특정 여행 내 아직 완료되지 않은 정산 건 존재 여부 확인용
    public List<SettlementVO> getPendingOrProcessingByTripId(@Param("tripId") int tripId);

    // DB insert용
    public void insertSettlement(@Param("vo") SettlementVO vo);
}
