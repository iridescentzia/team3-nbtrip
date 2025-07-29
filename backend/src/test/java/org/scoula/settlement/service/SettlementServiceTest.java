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
        int tripId = 4;
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



}