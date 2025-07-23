package org.scoula.settlement.service;

import org.scoula.settlement.domain.SettlementVO;

import java.util.List;

// 정산 상태 처리 비즈니스 로직
public interface SettlementService {

    // 조회 관련
    List<SettlementVO> getSettlementsByUserId(int userId); // 특정 유저가 관련된 모든 정산 내역 조회
    List<SettlementVO> getSettlementsByTripId(int tripId); // 특정 여행의 정산 내역 조회
    List<SettlementVO> getSettlementsByStatus(String status); // 상태별 정산 내역 필터링
    SettlementVO getById(int settlementId);

    // 상태 업데이트 관련
    int updateSettlementStatus(int settlementId, String newStatus); // 상태 전이(내부 관리용)
    boolean markAsCompleted(int settlementId); // 사용자 송금 완료 시점에만 사용 (API 노출용)
    List<SettlementVO> getPendingOrProcessingByTripId(int tripId); // 정산 상태 확인 (아직 완료되지 않은 정산 건 존재 여부 판단용)

}
