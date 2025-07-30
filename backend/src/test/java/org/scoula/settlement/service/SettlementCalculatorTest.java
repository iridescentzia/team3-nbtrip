package org.scoula.settlement.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.scoula.settlement.dto.SettlementDTO.RawSettlementDataDTO;
import org.scoula.settlement.dto.SettlementDTO.OptimizedTransaction;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

class SettlementCalculatorTest {

    private SettlementCalculator settlementCalculator;

    // 각 테스트가 실행되기 전에 SettlementCalculator 객체를 새로 생성
    @BeforeEach
    void setUp() {
        settlementCalculator = new SettlementCalculator();
    }

    @Test
    @DisplayName("상계 기반 직접 정산 알고리즘 테스트 (강릉 바다 여행)")
    void testCalculateForGangneungTrip() {
        // === GIVEN (주어진 상황) ===
        // 1. '강릉 바다 여행'에 참여한 멤버 ID 목록을 준비합니다.
        List<Integer> memberIds = Arrays.asList(3, 4, 5, 6, 8); // 최정훈, 권준호, 앨리스, 밥, 다이애나

        // 2. DB에서 가져왔다고 가정한 원본 데이터(rawData)를 직접 만듭니다.
        List<RawSettlementDataDTO> rawData = createGangneungTripMockData();

        // === WHEN (알고리즘 실행) ===
        // 준비된 데이터를 가지고 정산 계산 알고리즘을 실행합니다.
        List<OptimizedTransaction> finalTransactions = settlementCalculator.calculate(rawData, memberIds);

        // === THEN (결과 검증) ===
        // 1. 결과가 null이 아닌지, 총 송금 건수가 예상과 일치하는지 확인합니다.
        assertNotNull(finalTransactions, "최종 송금 목록은 null이 아니어야 합니다.");
        assertEquals(10, finalTransactions.size(), "상계 처리 후 총 송금 건수는 10건이어야 합니다.");

        // 2. 테스트의 편의성을 위해 List를 Map으로 변환합니다.
        // Key: "senderId->receiverId", Value: amount
        Map<String, Integer> transactionMap = finalTransactions.stream()
                .collect(Collectors.toMap(
                        tx -> tx.getSenderId() + "->" + tx.getReceiverId(),
                        OptimizedTransaction::getAmount
                ));

        // 3. 각 송금 내역의 금액이 정확한지 하나씩 검증합니다. (정확한 계산 결과로 수정)
        assertEquals(80000, transactionMap.get("4->3"));  // 권준호 -> 최정훈
        assertEquals(90000, transactionMap.get("5->3"));  // 앨리스 -> 최정훈
        assertEquals(104000, transactionMap.get("6->3")); // 밥 -> 최정훈
        assertEquals(96000, transactionMap.get("8->3"));  // 다이애나 -> 최정훈
        assertEquals(10000, transactionMap.get("5->4"));  // 앨리스 -> 권준호
        assertEquals(24000, transactionMap.get("6->4"));  // 밥 -> 권준호
        assertEquals(16000, transactionMap.get("8->4"));  // 다이애나 -> 권준호
        assertEquals(14000, transactionMap.get("6->5"));  // 밥 -> 앨리스
        assertEquals(6000, transactionMap.get("8->5"));   // 다이애나 -> 앨리스
        assertEquals(8000, transactionMap.get("6->8"));   // 밥 -> 다이애나


        System.out.println("✅ 상계 기반 직접 정산 알고리즘 테스트 성공!");
        System.out.println("--- 최종 송금 목록 (tripId=5) ---");
        transactionMap.forEach((key, value) -> System.out.println("  - " + key + ": " + value + "원"));
    }

    // '강릉 바다 여행' 시나리오의 원본 데이터를 생성하는 헬퍼 메소드
    private List<RawSettlementDataDTO> createGangneungTripMockData() {
        return Arrays.asList(
                // 펜션 (최정훈(3) 결제, 5명)
                createRawData(3, 4, 100000), createRawData(3, 5, 100000), createRawData(3, 6, 100000), createRawData(3, 8, 100000),
                // KTX (권준호(4) 결제, 5명)
                createRawData(4, 3, 40000), createRawData(4, 5, 40000), createRawData(4, 6, 40000), createRawData(4, 8, 40000),
                // 해산물 (앨리스(5) 결제, 5명)
                createRawData(5, 3, 30000), createRawData(5, 4, 30000), createRawData(5, 6, 30000), createRawData(5, 8, 30000),
                // 브런치 (밥(6) 결제, 5명)
                createRawData(6, 3, 16000), createRawData(6, 4, 16000), createRawData(6, 5, 16000), createRawData(6, 8, 16000),
                // 다이소 (다이애나(8) 결제, 5명)
                createRawData(8, 3, 24000), createRawData(8, 4, 24000), createRawData(8, 5, 24000), createRawData(8, 6, 24000),
                // 택시 (최정훈(3) 결제, 5명)
                createRawData(3, 4, 20000), createRawData(3, 5, 20000), createRawData(3, 6, 20000), createRawData(3, 8, 20000)
        );
    }

    // 테스트 데이터 생성을 돕는 작은 헬퍼 메소드
    private RawSettlementDataDTO createRawData(int payerId, int participantId, int amount) {
        RawSettlementDataDTO data = new RawSettlementDataDTO();
        data.setPayerId(payerId);
        data.setParticipantId(participantId);
        data.setSplitAmount(amount);
        return data;
    }
}