package org.scoula.payment.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.scoula.account.service.AccountService;
import org.scoula.merchant.service.MerchantService;
import org.scoula.payment.domain.ParticipantVO;
import org.scoula.payment.domain.PaymentType;
import org.scoula.payment.domain.PaymentVO;
import org.scoula.payment.dto.ParticipantDTO;
import org.scoula.payment.dto.PaymentDTO;
import org.scoula.payment.mapper.ParticipantMapper;
import org.scoula.payment.mapper.PaymentMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Log4j2
public class PaymentServiceImpl implements PaymentService {

    private final PaymentMapper paymentMapper;
    private final ParticipantMapper participantMapper;
    private final AccountService accountService;
    private final MerchantService merchantService;

    /* QR 결제 처리 */
    @Override
    @Transactional
    public void processPayment(PaymentDTO paymentDTO, int userId, int tripId) {
        validatePayment(paymentDTO, userId, tripId);

        // 결제 전 잔액 확인
        int beforeBalance = accountService.getBalanceByUserId(userId);

        // 사용자 계좌 잔액 차감
        accountService.decreaseUserBalance(userId, paymentDTO.getAmount());

        // 결제 후 잔액 확인(검증)
        int afterBalance = accountService.getBalanceByUserId(userId);
        if(afterBalance != beforeBalance - paymentDTO.getAmount()) {
            throw new RuntimeException("결제 금액 오류");
        }

        // 사업자 매출 반영
        merchantService.increaseSales(paymentDTO.getMerchantId(), paymentDTO.getAmount());

        // 결제 내역 저장
        PaymentVO paymentVO = savePaymentRecord(paymentDTO, PaymentType.QR, LocalDateTime.now(), paymentDTO.getMerchantId(), userId, tripId);

        // 결제 참여자 저장 및 금액 분배
        saveParticipants(paymentDTO, paymentVO, true, userId);
    }


    /* 선결제/기타 결제 수동 등록 */
    @Override
    @Transactional
    public void registerManualPayment(PaymentDTO paymentDTO, int userId, int tripId) {
        validatePayment(paymentDTO, userId, tripId);

        // 수동 결제 시 사용자가 날짜/시간 입력하면 병합, 아니면 현재 시간
        LocalDateTime payAt = (paymentDTO.getPaymentDate() != null && paymentDTO.getPaymentTime() != null)
                ? LocalDateTime.of(paymentDTO.getPaymentDate(), paymentDTO.getPaymentTime())
                : LocalDateTime.now();

        // 결제 내역 저장
        PaymentVO paymentVO = savePaymentRecord(paymentDTO, paymentDTO.getPaymentType(), payAt, null, userId, tripId);

        // 결제 참여자 저장 및 금액 분배
        saveParticipants(paymentDTO, paymentVO, false, userId);
    }


    /* 검증 */
    private void validatePayment(PaymentDTO paymentDTO, int userId, int tripId) {
        if (paymentDTO.getAmount() <= 0) {
            throw new IllegalArgumentException("결제 금액은 0원보다 커야 합니다.");
        }
        List<ParticipantDTO> participants = paymentDTO.getParticipants();
        if (participants == null || participants.isEmpty()) {
            throw new IllegalArgumentException("참여자는 최소 1명 이상이어야 합니다.");
        }
        boolean payerIncluded = participants.stream()
                .anyMatch(p -> p.getUserId() == userId);
        if (!payerIncluded) {
            throw new IllegalArgumentException("결제자는 반드시 결제 참여자에 포함되어야 합니다.");
        }
    }


    /* 결제 내역 저장 */
    private PaymentVO savePaymentRecord(PaymentDTO paymentDTO, PaymentType paymentType, LocalDateTime payAt, Integer merchantId, int userId, int tripId) {
        PaymentVO paymentVO = new PaymentVO();
        paymentVO.setTripId(tripId);
        paymentVO.setUserId(userId);
        paymentVO.setAmount(paymentDTO.getAmount());
        paymentVO.setPaymentType(paymentType);
        paymentVO.setPayAt(payAt);
        paymentVO.setMemo(paymentDTO.getMemo());
        paymentVO.setMerchantId(merchantId);

        int inserted = paymentMapper.insertPayment(paymentVO);
        if (inserted == 0) {
            throw new RuntimeException("결제 내역 저장 실패");
        }
        return paymentVO;
    }


    /* 결제 참여자 저장 및 금액 분배 */
    private void saveParticipants(PaymentDTO paymentDTO, PaymentVO paymentVO, boolean isAutoSplit, int userId) {
        List<ParticipantDTO> participants = paymentDTO.getParticipants();
        int participantCount = participants.size();
        int baseSplitAmount = paymentDTO.getAmount() / participantCount;
        int remainderAmount = paymentDTO.getAmount() % participantCount;

        boolean allSplitAmountZero = participants.stream()
                .allMatch(p -> p.getSplitAmount() == 0);

        List<ParticipantVO> participantVOList = participants.stream()
                .map(p -> {
                    int splitAmount;
                    if (isAutoSplit || allSplitAmountZero) {
                        splitAmount = baseSplitAmount;
                        if (Integer.valueOf(p.getUserId()).equals(userId)) {
                            splitAmount += remainderAmount;
                        }
                    } else {
                        splitAmount = p.getSplitAmount();
                    }
                    return ParticipantVO.builder()
                            .paymentId(paymentVO.getPaymentId())
                            .userId(p.getUserId())
                            .splitAmount(splitAmount)
                            .build();
                })
                .toList();

        int totalSplit = participantVOList.stream().mapToInt(ParticipantVO::getSplitAmount).sum();
        if (totalSplit != paymentDTO.getAmount()) {
            throw new RuntimeException("분배 금액 총합 오류");
        }

        int result = participantMapper.insertParticipants(participantVOList);
        if (result != participantCount) {
            log.error("참여자 저장 실패");
            throw new RuntimeException("결제 참여자 저장 실패");
        }
    }
}
