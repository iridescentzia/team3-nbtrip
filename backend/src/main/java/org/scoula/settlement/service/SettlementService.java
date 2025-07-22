package org.scoula.settlement.service;

import org.scoula.settlement.domain.SettlementVO;

import java.util.List;

// 정산 상태 처리 비즈니스 로직
public interface SettlementService {
    // 특정 유저가 관련된 모든 정산 내역 조회
    List<SettlementVO> getSettlementsByUserId(int userId);

    // 특정 여행의 정산 내역 조회
    List<SettlementVO> getSettlementsByTripId(int tripId);

    // 상태별 정산 내역 필터링 : pending, processing, completed
    List<SettlementVO> getSettlementsByStatus(String status);

    // 정산 상태 업데이트 : pending -> processing, processing -> completed
    int updateSettlementStatus(int settlementId, String newStatus);
}
