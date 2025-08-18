package org.scoula.settlement.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.scoula.settlement.domain.SettlementVO;
import org.scoula.settlement.dto.SettlementDTO;
import org.scoula.settlement.mapper.SettlementAccountMapper;
import org.scoula.settlement.mapper.SettlementMapper;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Log4j2
public class IndividualTransferProcessor {
    private final SettlementMapper mapper;
    private final SettlementAccountMapper settlementAccountMapper;

    @Transactional(propagation = Propagation.REQUIRES_NEW)  // 완전히 독립적인 트랜잭션
    public SettlementDTO.TransferDetail processTransfer(int settlementId, int userId) {
        SettlementDTO.TransferDetail detail = new SettlementDTO.TransferDetail();
        detail.setSettlementId(settlementId);

        try {
            // 1. 정산 정보 조회
            SettlementVO vo = mapper.selectForUpdateWithNicknames(settlementId); // 트랜잭션 안전성을 위한 배타적 락 적용
            if (vo == null) {
                return createFailureDetail(detail, "정산 정보 없음", "해당 정산 내역을 찾을 수 없습니다");
            }

            detail.setReceiverNickname(vo.getReceiverNickname());
            detail.setAmount(vo.getAmount());

            // 2. 권한 체크
            if (!vo.getSenderId().equals(userId)) {
                return createFailureDetail(detail, "권한없음", "송금 권한이 없습니다");
            }

            // 3. 상태 체크
            if (!"PENDING".equalsIgnoreCase(vo.getSettlementStatus())) {
                return createFailureDetail(detail, "상태오류", "이미 처리된 정산입니다");
            }

            // 4. 원자적 송금 처리 (실패 시 이 트랜잭션만 롤백)
            int debitResult = settlementAccountMapper.debitIfEnough(userId, vo.getAmount());
            if (debitResult == 0) {
                return createFailureDetail(detail, "잔액부족", "계좌 잔액을 확인해주세요");
            }

            // 5. 입금 처리
            int creditResult = settlementAccountMapper.credit(vo.getReceiverId(), vo.getAmount());
            if (creditResult == 0) {
                throw new RuntimeException("입금 실패. 데이터 정합성 오류");
            }

            // 6. COMPLETED 상태로 직접 업데이트 (자동 완료)
            int updated = mapper.updateSettlementStatus(settlementId, "COMPLETED");
            if (updated == 0) {
                throw new RuntimeException("상태 업데이트 실패");
            }

            // 성공 - 개별 트랜잭션 커밋
            detail.setSuccess(true);
            detail.setCurrentStatus("COMPLETED");

            log.info("✅ 개별 송금 완료 - ID: {}, 금액: {}", settlementId, vo.getAmount());
            return detail;

        } catch (Exception e) {
            log.error("개별 송금 실패 - settlementId: {}", settlementId, e);
            return createFailureDetail(detail, "시스템오류", "일시적 오류가 발생했습니다. 잠시 후 다시 시도해주세요");
        }
    }

    private SettlementDTO.TransferDetail createFailureDetail(SettlementDTO.TransferDetail detail,
                                                             String reason, String userMessage) {
        detail.setSuccess(false);
        detail.setFailureReason(reason);
        detail.setCurrentStatus("PENDING");  // 실패 시 상태 변경 없음

        return detail;
    }
}
