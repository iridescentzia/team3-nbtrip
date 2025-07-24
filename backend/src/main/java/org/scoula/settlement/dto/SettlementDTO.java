package org.scoula.settlement.dto;

import lombok.Data;
import java.util.List;


public class SettlementDTO {

    @Data
    public static class SettlementSummaryResponseDto {
        private String tripName;
        private int totalAmount;
        private List<MemberPaymentInfo> memberPayments;
    }


    @Data
    public static class MemberPaymentInfo {
        private String nickname;
        private int amount;
    }


    @Data
    public static class SettlementResultResponseDto {
        private int totalAmount;
        private List<OptimizedTransaction> transactions;
    }


    @Data
    public static class OptimizedTransaction {
        private String senderNickname;
        private String receiverNickname;
        private int amount;
    }

    @Data
    public static class PersonalSettlementResponseDto {
        private List<OptimizedTransaction> toReceive;
        private List<OptimizedTransaction> toSend;
    }

    @Data
    public static class CreateSettlementRequestDto {
        private Long tripId;
    }

    @Data
    public static class UpdateSettlementStatusRequestDto {
        private String status; // "PROCESSING" 또는 "COMPLETED"}
    }
}