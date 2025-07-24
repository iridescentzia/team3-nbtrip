package org.scoula.settlement.service;

import org.scoula.settlement.dto.SettlementDTO;
import org.springframework.stereotype.Service;

public interface SettlementService {

    /* 정산 1단계에 필요한 요약 정보를 조회함. */
    SettlementDTO.SettlementSummaryResponseDto getSettlementSummary(Long tripId);
}
