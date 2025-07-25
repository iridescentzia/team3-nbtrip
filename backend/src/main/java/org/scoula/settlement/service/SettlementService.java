package org.scoula.settlement.service;

import org.scoula.settlement.dto.SettlementDTO;
import org.springframework.stereotype.Service;

public interface SettlementService {

    /* 정산 1단계에 필요한 요약 정보를 조회함. */
    SettlementDTO.SettlementSummaryResponseDto getSettlementSummary(Long tripId);

    /* 정산 2단계: 최종 정산 결과를 계산하여 반환함. */
    SettlementDTO.SettlementResultResponseDto calculateFinalSettlement(Long tripId);
}
