package org.scoula.settlement.service;

import org.scoula.settlement.dto.SettlementDTO;
import org.springframework.stereotype.Service;
import org.scoula.settlement.domain.SettlementVO;
import java.util.List;

/**
 * 정산 상태 처리와 그룹원 간 송금 처리 비즈니스 로직
 */
public interface SettlementService {
  
    /* 정산 1단계에 필요한 요약 정보를 조회함. */
    SettlementDTO.SettlementSummaryResponseDto getSettlementSummary(Long tripId);

    /* 정산 2단계: 최종 정산 결과를 계산하여 반환함. */
    SettlementDTO.SettlementResultResponseDto calculateFinalSettlement(Long tripId);

    // ==================== 조회 관련 ====================

    /**
     * 특정 유저가 관련된 모든 정산 내역 조회
     */
    List<SettlementVO> getSettlementsByUserId(int userId);

    /**
     * 특정 여행의 모든 정산 내역 조회 (그룹장용 - 시나리오 2번)
     */
    List<SettlementVO> getSettlementsByTripId(int tripId);

    /**
     * 상태별 정산 내역 필터링
     */
    List<SettlementVO> getSettlementsByStatus(String status);

    /**
     * 정산 ID로 단건 조회
     */
    SettlementVO getById(int settlementId);

    /**
     * 개인별 정산 내역 조회 (시나리오 4번)
     * 내가 보내야 할 것 + 받아야 할 것 분리해서 반환
     */
    SettlementDTO.PersonalSettlementResponseDto getMySettlements(int userId, int tripId);

    /**
     * 특정 사용자가 보내야 할 정산들만 조회
     */
    List<SettlementVO> getMyOutgoingSettlements(int userId, int tripId);

    /**
     * 특정 사용자가 받아야 할 정산들만 조회
     */
    List<SettlementVO> getMyIncomingSettlements(int userId, int tripId);

    // ==================== 정산 요청 생성 ====================

    /**
     * 정산 요청 생성 (시나리오 3번)
     * n빵 계산 결과를 받아서 정산 row들을 PENDING 상태로 생성
     */
    SettlementDTO.CreateSettlementResponseDto createSettlementRequest(int userId, int tripId);

    /**
     * n빵 분배 결과를 DB에 저장
     */
    void saveCalculatedResults(List<SettlementDTO.OptimizedTransaction> results, int tripId);

    /**
     * 그룹장 권한 체크
     */
    boolean canRequestSettlement(int userId, int tripId);

    // ==================== 상태 업데이트 관련 ====================

    /**
     * 정산 상태 업데이트 (내부 관리용)
     */
    int updateSettlementStatus(int settlementId, String newStatus);

    /**
     * 송금 완료 처리 (시나리오 5번 두번째 단계)
     * PROCESSING → COMPLETED 상태 변경
     */
    SettlementDTO.CompleteSettlementResponseDto markAsCompleted(int settlementId, int userId);

    /**
     * 송금 시작 권한 체크
     */
    boolean canStartTransfer(int settlementId, int userId);

    // ==================== 송금 처리 ====================

    /**
     * 그룹원 간 송금 처리 (시나리오 5번 첫번째 단계)
     * PENDING → PROCESSING 상태 변경 + 실제 잔액 이동
     */
    SettlementDTO.TransferResponseDto transferToUser(int settlementId, int userId);

    // ==================== 상태 확인 ====================

    /**
     * 특정 여행 내 미완료된 정산 목록 조회
     */
    List<SettlementVO> getPendingOrProcessingByTripId(int tripId);

    /**
     * 사용자의 전체 정산 상태 조회 (시나리오 6,7번)
     */
    SettlementDTO.MySettlementStatusResponseDto getMyOverallSettlementStatus(int userId);

    /**
     * 특정 여행의 미정산 존재 여부
     */
    SettlementDTO.RemainingSettlementResponseDto getRemainingSettlements(int tripId);
}
