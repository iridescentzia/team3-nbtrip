package org.scoula.settlement.service;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.scoula.settlement.dto.SettlementDTO;
import java.util.Arrays;
import java.util.List;

class SettlementCalculatorTest {

    private SettlementCalculator calculator;

    // 각 테스트가 실행되기 전에 SettlementCalculator 인스턴스를 생성
    @BeforeEach
    void setUp() {
        calculator = new SettlementCalculator();
    }

    @Test
    @DisplayName("tripId=4 시나리오: 복잡한 채무 관계가 올바르게 상계 처리되는지 테스트한다")
    void calculate_복잡한_정산_테스트() {
        // Minsu(1), gunwoo(2), JungHoon(3), junho(4)
        List<Integer> memberIds = Arrays.asList(1, 2, 3, 4);

        // 가짜 결제 원본 데이터 생성
        // 1. Minsu가 40000원 결제 (참여자: 모두) -> 각자 10000원씩 부담
        //    -> gunwoo, JungHoon, junho는 Minsu에게 10000원씩 빚짐
        // 2. gunwoo가 30000원 결제 (참여자: gunwoo, JungHoon) -> 각자 15000원씩 부담
        //    -> JungHoon은 gunwoo에게 15000원 빚짐
        List<SettlementDTO.RawSettlementDataDTO> rawData = Arrays.asList(
                // --- 1번 결제 (Minsu가 4만원 결제) ---
                createRawData(1, 1, 10000), // Minsu 본인 부담
                createRawData(1, 2, 10000), // gunwoo 부담
                createRawData(1, 3, 10000), // JungHoon 부담
                createRawData(1, 4, 10000), // junho 부담
                // --- 2번 결제 (gunwoo가 3만원 결제) ---
                createRawData(2, 2, 15000), // gunwoo 본인 부담
                createRawData(2, 3, 15000)  // JungHoon 부담
        );

        // When: 실제 테스트할 메소드 호출
        List<SettlementDTO.OptimizedTransaction> result = calculator.calculate(rawData, memberIds);

        // Then: 결과 검증 (총 3번의 송금이 발생해야 함)
        // 1. JungHoon -> Minsu: 10000원
        // 2. JungHoon -> gunwoo: 15000원
        // 3. junho -> Minsu: 10000원
        assertEquals(4, result.size());

        assertTrue(result.stream().anyMatch(tx ->
                tx.getSenderId() == 3 && tx.getReceiverId() == 1 && tx.getAmount() == 10000
        ), "JungHoon이 Minsu에게 10000원을 보내야 합니다.");

        assertTrue(result.stream().anyMatch(tx ->
                tx.getSenderId() == 3 && tx.getReceiverId() == 2 && tx.getAmount() == 15000
        ), "JungHoon이 gunwoo에게 15000원을 보내야 합니다.");

        assertTrue(result.stream().anyMatch(tx ->
                tx.getSenderId() == 4 && tx.getReceiverId() == 1 && tx.getAmount() == 10000
        ), "junho가 Minsu에게 10000원을 보내야 합니다.");

        System.out.println("테스트 성공: 단순 채무 관계가 올바르게 계산되었습니다.");
        result.forEach(tx ->
                System.out.printf("송금: %d -> %d, 금액: %d원\n", tx.getSenderId(), tx.getReceiverId(), tx.getAmount())
        );
    }

    // 테스트 데이터 생성을 위한 헬퍼 메소드
    private SettlementDTO.RawSettlementDataDTO createRawData(int payerId, int participantId, int splitAmount) {
        SettlementDTO.RawSettlementDataDTO data = new SettlementDTO.RawSettlementDataDTO();
        data.setPayerId(payerId);
        data.setParticipantId(participantId);
        data.setSplitAmount(splitAmount);
        return data;
    }
}
