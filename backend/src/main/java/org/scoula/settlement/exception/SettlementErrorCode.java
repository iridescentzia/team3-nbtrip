package org.scoula.settlement.exception;

public enum SettlementErrorCode {
    INSUFFICIENT_BALANCE("잔액이 부족합니다. 필요: %d원, 보유: %d원"),
    UNAUTHORIZED_ACCESS("권한이 없습니다. 사용자 %d는 정산 %d에 대한 권한이 없습니다"),
    INVALID_STATUS("유효하지 않은 상태입니다. 현재: %s, 요청: %s"),
    SETTLEMENT_NOT_FOUND("정산 내역을 찾을 수 없습니다. ID: %d"),
    ALREADY_PROCESSED("이미 처리된 정산입니다. ID: %d, 상태: %s"),
    TRANSFER_FAILED("송금 처리 실패. 정산 ID: %d, 사유: %s"),
    GROUP_LEADER_ONLY("그룹장만 정산을 요청할 수 있습니다"),
    DUPLICATE_SETTLEMENT("이미 정산이 요청된 여행입니다"),
    BATCH_VALIDATION_FAILED("다중 송금 사전 검증 실패");

    private final String messageTemplate;

    SettlementErrorCode(String messageTemplate) {
        this.messageTemplate = messageTemplate;
    }

    public String getMessageTemplate() {
        return messageTemplate;
    }
}
