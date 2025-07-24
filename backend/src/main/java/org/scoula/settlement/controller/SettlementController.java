package org.scoula.settlement.controller;

import lombok.RequiredArgsConstructor;
import org.scoula.settlement.dto.SettlementDTO;
import org.scoula.settlement.service.SettlementService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/settlements")
@RequiredArgsConstructor
public class SettlementController {

    private final SettlementService settlementService;

    /*
     * 정산 1단계: 정산 요약 정보 조회 API
     * 특정 여행의 총 사용 금액과 멤버별 총 결제 금액을 조회함.
     */
    @GetMapping("/{tripId}/summary")
    public ResponseEntity<SettlementDTO.SettlementSummaryResponseDto> getSettlementSummary(@PathVariable Long tripId) {
        // SettlementService를 호출하여 비즈니스 로직을 수행
        SettlementDTO.SettlementSummaryResponseDto summaryDto = settlementService.getSettlementSummary(tripId);

        // 조회된 데이터를 ResponseEntity에 담아 프론트엔드로 반환
        return ResponseEntity.ok(summaryDto);
    }
}
