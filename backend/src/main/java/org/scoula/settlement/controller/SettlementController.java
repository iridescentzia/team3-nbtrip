package org.scoula.settlement.controller;

import lombok.RequiredArgsConstructor;
import org.scoula.settlement.dto.SettlementDTO;
import org.scoula.settlement.service.SettlementService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 정산 관련 API 요청을 처리하는 컨트롤러.
 */
@RestController
// FIX: API 경로를 프론트엔드 요청과 일치하도록 수정.
@RequestMapping("/api/settlements")
@RequiredArgsConstructor
public class SettlementController {

    private final SettlementService settlementService;

    /**
     * 정산 1단계: 정산 요약 정보 조회 API
     * 특정 여행의 총 사용 금액과 멤버별 총 결제 금액을 조회함.
     *
     * @param tripId 조회할 여행의 ID
     * @return ResponseEntity<SettlementSummaryResponseDto> 정산 요약 정보 DTO를 담은 응답
     */
    // FIX: API 경로를 프론트엔드 요청과 일치하도록 수정.
    @GetMapping("/{tripId}/summary")
    public ResponseEntity<SettlementDTO.SettlementSummaryResponseDto> getSettlementSummary(@PathVariable Long tripId) {
        // SettlementService를 호출하여 비즈니스 로직을 수행
        SettlementDTO.SettlementSummaryResponseDto summaryDto = settlementService.getSettlementSummary(tripId);

        // 조회된 데이터를 ResponseEntity에 담아 프론트엔드로 반환
        return ResponseEntity.ok(summaryDto);
    }

    /**
     * [NEW] 정산 2단계: 최종 정산 결과 계산 API
     * 상계 처리가 완료된 최적의 송금 목록을 조회함.
     *
     * @param tripId 조회할 여행의 ID
     * @return ResponseEntity<SettlementResultResponseDto> 최종 송금 목록을 담은 DTO
     */
    @GetMapping("/{tripId}/calculate")
    public ResponseEntity<SettlementDTO.SettlementResultResponseDto> calculateFinalSettlement(@PathVariable Long tripId) {
        // SettlementService를 호출하여 최종 정산 결과를 계산
        SettlementDTO.SettlementResultResponseDto resultDto = settlementService.calculateFinalSettlement(tripId);

        // 계산된 결과를 ResponseEntity에 담아 프론트엔드로 반환
        return ResponseEntity.ok(resultDto);
    }
}
