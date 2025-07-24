package org.scoula.settlement.dto;

import lombok.Data;
import java.util.List;

/**
 * 정산 관련 데이터를 전송하는 데 사용되는 DTO(Data Transfer Object) 클래스들을 포함함.
 */
public class SettlementDTO {
    /**
     * 정산 요약 정보 DTO (1단계 페이지용)
     * 여행의 총 사용 금액과 멤버별 총 결제액 정보를 담음.
     */
    @Data
    public static class SettlementSummaryResponseDto {
        private String tripName;
        private int totalAmount;
        private List<MemberPaymentInfo> memberPayments;
    }

    /**
     * 멤버별 결제 정보 DTO
     */
    @Data
    public static class MemberPaymentInfo {
        private String nickname;
        private int amount;
    }

    /**
     * 최종 정산 결과 DTO (2단계, 3단계 페이지용)
     * 최적화된 최종 송금 목록 전체를 담음.
     */
    @Data
    public static class SettlementResultResponseDto {
        private int totalAmount;
        private List<OptimizedTransaction> transactions;
    }

    /**
     * 최적화된 개별 송금 거래 DTO
     * "누가 누구에게 얼마를 보내야 하는지"에 대한 정보를 담음.
     */
    @Data
    public static class OptimizedTransaction {
        private String senderNickname;
        private String receiverNickname;
        private int amount;
    }

    /**
     * 개인화된 정산 내역 DTO (3단계 페이지용)
     * 로그인한 사용자를 기준으로 '받을 돈'과 '보낼 돈' 목록을 담음.
     */
    @Data
    public static class PersonalSettlementResponseDto {
        private List<OptimizedTransaction> toReceive;
        private List<OptimizedTransaction> toSend;
    }

    /**
     * 정산 요청 생성 DTO (POST /api/settlements)
     * 정산을 생성할 때 필요한 tripId를 담음.
     */
    @Data
    public static class CreateSettlementRequestDto {
        private Long tripId;
    }

    /**
     * 정산 상태 업데이트 DTO (PUT /api/settlements/{settlementId}/status)
     * 정산 상태를 변경할 때 필요한 status 정보를 담음.
     */
    @Data
    public static class UpdateSettlementStatusRequestDto {
        private String status; // "PROCESSING" 또는 "COMPLETED"
    }
}
