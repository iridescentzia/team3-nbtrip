package org.scoula.settlement.mapper;

import org.scoula.settlement.domain.SettlementVO;

import java.util.List;

// mybatis 정산 매퍼, SettlementMapper.xml과 연동하여 sql 실행
public interface SettlementMapper {
    // 특정 유저가 참여한 모든 정산 내역 조회
    public List<SettlementVO> getSettlementsByUserId(int userId);

    // 특정 여행의 정산 내역 조회
    public List<SettlementVO> getSettlementsByTripId(int tripId);

    // 상태별 정산 내역 필터링 조회 : pending, processing, completed
    public List<SettlementVO> getSettlementsByStatus(String status);

    // 정산 상태 업데이트 : pending -> processing
    public int updateSettlementStatus(int settlementId, String newStatus);

    // 정산 ID로 단건 조회 : 상태 확인 또는 상세 조회용
    public SettlementVO getById(int settlementId);

    // 특정 여행 내 아직 완료되지 않은 정산 건 존재 여부 확인용
    public List<SettlementVO> getPendingOrProcessingByTripId(int tripId);
}
