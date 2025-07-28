package org.scoula.settlement.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SettlementVO {
    private Integer settlementId; // 정산 ID (PK)
    private Integer tripId; // 여행 그룹 ID (FK)
    private Integer senderId; // 돈을 보내야 하는 사용자 ID (FK)
    private Integer receiverId; // 돈을 받아야 하는 사용자 ID (FK)
    private String settlementStatus; // 정산 상태 - pending, processing, completed
    private Integer amount; // 정산 금액

    // JOIN 쿼리로 조회된 닉네임 필드 추가 (INSERT 시에는 null)
    private String senderNickname;   // 송금자 닉네임
    private String receiverNickname; // 수신자 닉네임
}
