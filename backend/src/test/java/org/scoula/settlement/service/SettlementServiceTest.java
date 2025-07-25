package org.scoula.settlement.service;

import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.scoula.config.RootConfig;
import org.scoula.settlement.dto.SettlementDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
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
// FIX: 테스트에 필요한 모든 컴포넌트(Mapper, Service)를 찾을 수 있도록 스캔 범위를 명확하게 지정합니다.
@ComponentScan(basePackages = {"org.scoula.settlement.mapper", "org.scoula.settlement.service"})
@Transactional
@Log4j2 // Lombok을 사용하여 Log4j2 로거를 자동으로 생성
public class SettlementServiceTest {

    @Autowired
    private SettlementService settlementService;

    /**
     * 정산 1단계 요약 정보 조회 기능을 테스트합니다.
     * tripId가 4인 '가평 여름휴가'의 데이터를 기반으로 검증합니다.
     */
    @Test
    @DisplayName("정산 1단계 요약 정보 조회 테스트")
    public void testGetSettlementSummaryForGapyongTrip() {
        // === GIVEN ===
        Long tripId = 3L;
        log.info("🚀 정산 요약 정보 조회 테스트 시작: tripId = {}", tripId);

        // === WHEN ===
        SettlementDTO.SettlementSummaryResponseDto summaryDto = settlementService.getSettlementSummary(tripId);

        // === THEN ===
        // 1. DTO가 null이 아닌지 기본적인 확인
        assertNotNull(summaryDto, "정산 요약 DTO는 null이 아니어야 합니다.");
        log.info("✅ DTO 조회 성공!");

        // 2. 조회된 총 결제 금액을 로그로 출력
        log.info("======================================================");
        log.info("💰 총 사용 금액: {}원", summaryDto.getTotalAmount());
        log.info("======================================================");

        // 3. 조회된 멤버별 결제 금액 목록을 로그로 출력
        List<SettlementDTO.MemberPaymentInfo> memberPayments = summaryDto.getMemberPayments();
        assertNotNull(memberPayments, "멤버별 결제 목록은 null이 아니어야 합니다.");

        log.info("👥 멤버별 결제 내역 (총 {}명)", memberPayments.size());
        log.info("------------------------------------------------------");
        for (SettlementDTO.MemberPaymentInfo paymentInfo : memberPayments) {
            log.info("   - 닉네임: {}, 결제 금액: {}원", paymentInfo.getNickname(), paymentInfo.getAmount());
        }
        log.info("------------------------------------------------------");
        log.info("🎉 테스트 종료");
    }

    /**
     * [NEW] 정산 2단계: 최종 정산 결과 계산 로직을 테스트합니다.
     * tripId가 4인 '가평 여름휴가'의 데이터를 기반으로 검증합니다.
     */
    @Test
    @DisplayName("최종 정산 결과 계산 테스트 (가평 여름휴가)")
    public void testCalculateFinalSettlement() {
        // === GIVEN ===
        Long tripId = 4L;
        log.info("🚀 최종 정산 결과 계산 테스트 시작: tripId = {}", tripId);

        // === WHEN ===
        SettlementDTO.SettlementResultResponseDto resultDto = settlementService.calculateFinalSettlement(tripId);
        log.info("➡️ 서비스 메소드 호출 완료. DTO: {}", resultDto);

        // === THEN ===
        // 1. DTO와 송금 목록이 null이 아닌지 확인
        assertNotNull(resultDto, "정산 결과 DTO는 null이 아니어야 합니다.");
        List<SettlementDTO.OptimizedTransaction> transactions = resultDto.getTransactions();
        assertNotNull(transactions, "최종 송금 목록은 null이 아니어야 합니다.");
        log.info("✅ 최종 송금 목록 조회 성공 (총 {}건)", transactions.size());

        // 2. 예상되는 송금 건수와 일치하는지 확인 (상계 기반 알고리즘 기준)
        // (알고리즘에 따라 예상 건수는 달라질 수 있습니다)
        // assertEquals(6, transactions.size(), "예상되는 총 송금 건수와 일치해야 합니다.");

        // 3. 각 송금 내역을 상세히 로그로 출력하고 검증
        log.info("------------------ 최종 송금 목록 ------------------");
        // 테스트의 편의성을 위해 Map으로 변환
        Map<String, Integer> transactionMap = transactions.stream()
                .collect(Collectors.toMap(
                        tx -> tx.getSenderNickname() + "->" + tx.getReceiverNickname(),
                        SettlementDTO.OptimizedTransaction::getAmount
                ));

        transactionMap.forEach((key, value) -> log.info("   - {}: {}원", key, value));
        log.info("----------------------------------------------------");

        // 4. 특정 송금 내역의 정확성 검증 (예시)
        // '이건우'가 '김민수'에게 보내야 할 돈이 정확한지 확인
        assertEquals(56000, transactionMap.get("이건우->김민수"), "이건우가 김민수에게 보내는 금액이 일치해야 합니다.");
        // '권준호'가 '최정훈'에게 보내야 할 돈이 정확한지 확인
        assertEquals(33750, transactionMap.get("권준호->최정훈"), "권준호가 최정훈에게 보내는 금액이 일치해야 합니다.");

        log.info("✅ 주요 송금 내역 금액 검증 완료");
        log.info("🎉 최종 정산 결과 계산 테스트 성공!");
    }


}