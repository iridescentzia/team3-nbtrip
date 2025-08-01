package org.scoula.settlement.service;

import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.scoula.config.RootConfig;
import org.scoula.settlement.domain.SettlementVO;
import org.scoula.settlement.dto.SettlementDTO;
import org.scoula.settlement.mapper.SettlementMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.annotation.Commit;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

/**
 * SettlementService의 기능, 특히 정산 1단계 요약 정보 조회를 테스트하는 클래스.
 * Log4j2를 사용하여 테스트 과정을 로깅함.
 */
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = { RootConfig.class })
@WebAppConfiguration
// FIX: 테스트에 필요한 모든 컴포넌트(Mapper, Service)를 찾을 수 있도록 스캔 범위를 명확하게 지정합니다.
@ComponentScan(basePackages = {"org.scoula.settlement.mapper", "org.scoula.settlement.service","org.scoula.trip.mapper", "org.scoula.member.mapper"})
@Transactional
@Log4j2 // Lombok을 사용하여 Log4j2 로거를 자동으로 생성
public class SettlementServiceTest {

    @Autowired
    private SettlementService settlementService;

    @Autowired
    private SettlementMapper settlementMapper; // DB 검증을 위해 Mapper를 직접 주입

    private SettlementCalculator settlementCalculator;

    @BeforeEach
    void setUp() {
        settlementCalculator = new SettlementCalculator();
    }

    /**
     * [NEW] createSettlementRequest와 saveCalculatedResults 통합 테스트
     * 그룹장이 정산 요청 시, 계산된 결과가 settlement 테이블에 정확히 INSERT 되는지 검증합니다.
     */
    @Test
    @Commit
    @DisplayName("정산 요청 생성 및 DB 저장 통합 테스트 (강릉 바다 여행)")
    void testCreateSettlementRequestAndSave() {
        // === GIVEN (주어진 상황) ===
        int tripId = 5; // '강릉 바다 여행'
        int ownerId = 3; // 방장 '최정훈'

        // === WHEN (서비스 실행) ===
        // 그룹장이 정산 요청을 보내는 서비스를 실행합니다.
        SettlementDTO.CreateSettlementResponseDto response = settlementService.createSettlementRequest(ownerId, tripId);

        // === THEN (결과 검증) ===
        // 1. 서비스가 성공적으로 실행되었는지 확인합니다.
        assertTrue(response.isSuccess(), "정산 요청 생성은 성공해야 합니다.");
        assertEquals(10, response.getCreatedCount(), "생성된 정산 건수는 10건이어야 합니다.");

        // 2. 실제로 DB에 저장된 데이터를 직접 조회합니다.
        List<SettlementVO> savedSettlements = settlementMapper.getSettlementsByTripId(tripId);
        assertNotNull(savedSettlements, "저장된 정산 내역은 null이 아니어야 합니다.");
        assertEquals(10, savedSettlements.size(), "DB에 저장된 정산 건수는 10건이어야 합니다.");

        // 3. 테스트의 편의성을 위해 List를 Map으로 변환합니다.
        Map<String, Integer> transactionMap = savedSettlements.stream()
                .collect(Collectors.toMap(
                        tx -> tx.getSenderId() + "->" + tx.getReceiverId(),
                        SettlementVO::getAmount
                ));

        // 4. DB에 저장된 각 송금 내역의 금액이 정확한지 검증합니다.
        assertEquals(80000, transactionMap.get("4->3"));  // 권준호 -> 최정훈
        assertEquals(90000, transactionMap.get("5->3"));  // 앨리스 -> 최정훈
        assertEquals(104000, transactionMap.get("6->3")); // 밥 -> 최정훈
        assertEquals(96000, transactionMap.get("8->3"));  // 다이애나 -> 최정훈
        assertEquals(10000, transactionMap.get("5->4"));  // 앨리스 -> 권준호
        assertEquals(24000, transactionMap.get("6->4"));  // 밥 -> 권준호
        assertEquals(16000, transactionMap.get("8->4"));  // 다이애나 -> 권준호
        assertEquals(14000, transactionMap.get("6->5"));  // 밥 -> 앨리스
        assertEquals(6000, transactionMap.get("8->5"));   // 다이애나 -> 앨리스
        assertEquals(8000, transactionMap.get("8->6"));   // 다이애나 -> 밥

        System.out.println("✅ 정산 요청 및 DB 저장 통합 테스트 성공!");
        System.out.println("--- DB에 저장된 송금 목록 (tripId=5) ---");
        transactionMap.forEach((key, value) -> System.out.println("  - " + key + ": " + value + "원"));
    }

}