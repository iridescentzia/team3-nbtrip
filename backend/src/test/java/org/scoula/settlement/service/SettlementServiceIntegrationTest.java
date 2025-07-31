package org.scoula.settlement.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;

import org.scoula.settlement.dto.SettlementDTO;
import org.scoula.settlement.domain.SettlementVO;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(locations = {
        "classpath:spring/root-context.xml"        // 실제 설정 파일 경로로 수정
})
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Transactional
public class SettlementServiceIntegrationTest {

    @Autowired
    private SettlementService settlementService;

    // 테스트용 상수
    private static final int TRIP_ID = 1;           // 서울 우정 여행
    private static final int GROUP_LEADER_ID = 1;   // 김민수 (그룹장)
    private static final int MEMBER_ID = 4;         // 권준호 (그룹원)

    @Test
    @Order(1)
    @DisplayName("1단계: 정산 요약 정보 조회 테스트")
    void testGetSettlementSummary() {
        // Given & When
        SettlementDTO.SettlementSummaryResponseDto summary =
                settlementService.getSettlementSummary(TRIP_ID);

        // Then
        assertThat(summary).isNotNull();
        assertThat(summary.getTripName()).isEqualTo("서울 우정 여행");
        assertThat(summary.getTotalAmount()).isEqualTo(627000);
        assertThat(summary.getMemberPayments()).hasSize(4);

        System.out.println("🔍 정산 요약:");
        System.out.println("여행명: " + summary.getTripName());
        System.out.println("총 금액: " + summary.getTotalAmount());
        summary.getMemberPayments().forEach(member ->
                System.out.println("  " + member.getNickname() + ": " + member.getAmount() + "원")
        );
    }

    @Test
    @Order(2)
    @DisplayName("2단계: 최종 정산 결과 계산 테스트")
    void testCalculateFinalSettlement() {
        // Given & When
        SettlementDTO.SettlementResultResponseDto result =
                settlementService.calculateFinalSettlement(TRIP_ID);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getTripName()).isEqualTo("서울 우정 여행");
        assertThat(result.getMembers()).hasSize(4);

        System.out.println("🧮 최종 정산 계산 결과:");
        System.out.println("여행명: " + result.getTripName());
        System.out.println("총 금액: " + result.getTotalAmount());
        System.out.println("멤버: " + result.getMembers());
        System.out.println("거래 내역:");
        result.getTransactions().forEach(tx ->
                System.out.println("  " + tx.getSenderNickname() +
                        " → " + tx.getReceiverNickname() +
                        ": " + tx.getAmount() + "원")
        );
    }

    @Test
    @Order(3)
    @DisplayName("3단계: 정산 요청 생성 테스트")
    void testCreateSettlementRequest() {
        // Given & When
        SettlementDTO.CreateSettlementResponseDto response =
                settlementService.createSettlementRequest(GROUP_LEADER_ID, TRIP_ID);

        // Then
        assertThat(response.isSuccess()).isTrue();
        assertThat(response.getCreatedCount()).isGreaterThan(0);
        assertThat(response.getSettlements()).isNotEmpty();

        System.out.println("🎯 정산 생성 결과:");
        System.out.println("성공 여부: " + response.isSuccess());
        System.out.println("생성 건수: " + response.getCreatedCount());
        System.out.println("메시지: " + response.getMessage());

        response.getSettlements().forEach(settlement ->
                System.out.println("  송금자ID: " + settlement.getSenderId() +
                        " → 수신자ID: " + settlement.getReceiverId() +
                        " (금액: " + settlement.getAmount() + "원)")
        );
    }

    @Test
    @Order(4)
    @DisplayName("4단계: 생성된 정산 내역 조회 테스트")
    void testGetSettlementsByTripId() {
        // 먼저 정산 생성
        settlementService.createSettlementRequest(GROUP_LEADER_ID, TRIP_ID);

        // Given & When
        List<SettlementVO> settlements = settlementService.getSettlementsByTripId(TRIP_ID);

        // Then
        assertThat(settlements).isNotEmpty();
        assertThat(settlements).allMatch(s -> s.getTripId().equals(TRIP_ID));
        assertThat(settlements).allMatch(s -> "PENDING".equals(s.getSettlementStatus()));

        System.out.println("📋 생성된 정산 내역:");
        settlements.forEach(settlement ->
                System.out.println("  ID: " + settlement.getSettlementId() +
                        ", " + settlement.getSenderNickname() +
                        " → " + settlement.getReceiverNickname() +
                        " (" + settlement.getAmount() + "원, " + settlement.getSettlementStatus() + ")")
        );
    }

    @Test
    @Order(5)
    @DisplayName("5단계: 개인별 정산 내역 조회 테스트 (권준호)")
    void testGetMySettlements() {
        // 먼저 정산 생성
        settlementService.createSettlementRequest(GROUP_LEADER_ID, TRIP_ID);

        // Given & When
        SettlementDTO.PersonalSettlementResponseDto personalSettlement =
                settlementService.getMySettlements(MEMBER_ID, TRIP_ID);

        // Then
        assertThat(personalSettlement).isNotNull();

        System.out.println("👤 권준호의 개인 정산:");
        System.out.println("전체 상태: " + personalSettlement.getOverallStatus());

        System.out.println("보낼 돈:");
        personalSettlement.getToSend().forEach(settlement ->
                System.out.println("  → 받는이: " + settlement.getReceiverNickname() +
                        ", 금액: " + settlement.getAmount() + "원 (" + settlement.getStatus() + ")")
        );

        System.out.println("받을 돈:");
        personalSettlement.getToReceive().forEach(settlement ->
                System.out.println("  ← 보내는이: " + settlement.getSenderNickname() +
                        ", 금액: " + settlement.getAmount() + "원 (" + settlement.getStatus() + ")")
        );
    }

    @Test
    @Order(6)
    @DisplayName("6단계: 송금 처리 테스트")
    void testTransferToUsers() {
        // 먼저 정산 생성
        settlementService.createSettlementRequest(GROUP_LEADER_ID, TRIP_ID);

        // Given - 권준호가 보낼 정산 ID들 조회
        SettlementDTO.PersonalSettlementResponseDto personalSettlement =
                settlementService.getMySettlements(MEMBER_ID, TRIP_ID);

        List<Integer> settlementIdsToTransfer = personalSettlement.getToSend().stream()
                .map(SettlementDTO.OptimizedTransactionWithNickname::getSettlementId)
                .toList();

        assertThat(settlementIdsToTransfer).isNotEmpty();

        // When - ✅ 실제 메서드명 사용
        SettlementDTO.TransferResponseDto transferResponse =
                settlementService.transferToUsers(settlementIdsToTransfer, MEMBER_ID);

        // Then
        assertThat(transferResponse.isSuccess()).isTrue();
        assertThat(transferResponse.getSuccessCount()).isGreaterThan(0);

        System.out.println("💸 송금 처리 결과:");
        System.out.println("전체 성공: " + transferResponse.isSuccess());
        System.out.println("성공 건수: " + transferResponse.getSuccessCount() + "/" + transferResponse.getTotalCount());
        System.out.println("송금 후 잔액: " + transferResponse.getSenderBalance() + "원");
        System.out.println("메시지: " + transferResponse.getMessage());

        transferResponse.getDetails().forEach(detail ->
                System.out.println("  " + detail.getReceiverNickname() +
                        ": " + detail.getAmount() + "원 " +
                        (detail.isSuccess() ? "✅성공" : "❌실패(" + detail.getFailureReason() + ")"))
        );
    }

    @Test
    @Order(7)
    @DisplayName("7단계: 미정산 확인 테스트")
    void testGetRemainingSettlements() {
        // 먼저 정산 생성
        settlementService.createSettlementRequest(GROUP_LEADER_ID, TRIP_ID);

        // Given & When
        SettlementDTO.RemainingSettlementResponseDto remaining =
                settlementService.getRemainingSettlements(TRIP_ID);

        // Then
        assertThat(remaining).isNotNull();

        System.out.println("📊 미정산 현황:");
        System.out.println("미정산 존재: " + remaining.isHasRemaining());
        System.out.println("총 건수: " + remaining.getTotalCount());
        System.out.println("PENDING: " + remaining.getPendingCount());
        System.out.println("PROCESSING: " + remaining.getProcessingCount());
        System.out.println("COMPLETED: " + remaining.getCompletedCount());
    }
}