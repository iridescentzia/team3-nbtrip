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

    // QR 결제 처리
    @Override
    @Transactional
    public void processPayment(PaymentDTO paymentDTO) {
        if(paymentDTO.getAmount() <= 0) {
            throw new IllegalArgumentException("결제 금액은 0원보다 커야 합니다.");
        }

        List<ParticipantDTO> participants = paymentDTO.getParticipants();
        if(participants == null || participants.isEmpty()) {
            throw new IllegalArgumentException("참여자는 최소 1명 이상이어야 합니다.");
        }

        // 결제 전 잔액 확인
        int beforeBalance = accountService.getBalanceByUserId(paymentDTO.getUserId());

        // 사용자 계좌 잔액 차감
        accountService.decreaseUserBalance(paymentDTO.getUserId(), paymentDTO.getAmount());;

        // 결제 후 잔액 확인(검증)
        int afterBalance = accountService.getBalanceByUserId(paymentDTO.getUserId());
        if(afterBalance != beforeBalance - paymentDTO.getAmount()) {
            throw new RuntimeException("결제 금액 오류");
        }

        // 사업자 매출 반영
        merchantService.increaseSales(paymentDTO.getMerchantId(), paymentDTO.getAmount());

        // 결제 내역 저장
        PaymentVO paymentVO = new PaymentVO();
        paymentVO.setTripId(paymentDTO.getTripId());
        paymentVO.setUserId(paymentDTO.getUserId());
        paymentVO.setMerchantId(paymentDTO.getMerchantId());
        paymentVO.setAmount(paymentDTO.getAmount());
        paymentVO.setPaymentType(PaymentType.QR);
        paymentVO.setPayAt(LocalDateTime.now());

        int inserted = paymentMapper.insertPayment(paymentVO);
        if(inserted == 0) {
            log.error("결제 내역 저장 실패");
            throw new RuntimeException("결제 정보 저장 실패");
        }

        // participant 저장 - 금액 분배
        int participantCount = participants.size();
        int baseSplitAmount = paymentDTO.getAmount() / participantCount;
        int remainderAmount = paymentDTO.getAmount() % participantCount;

        List<ParticipantVO> participantVOList = participants.stream()
                .map(p -> {
                    // 금액 분배 시 결제자가 잔액 1-2원 더 부담
                    int splitAmount = baseSplitAmount;
                    if(Integer.valueOf(p.getUserId()).equals(paymentDTO.getUserId())) {
                        splitAmount += remainderAmount;
                    }
                    return ParticipantVO.builder()
                            .paymentId(paymentVO.getPaymentId())
                            .userId(p.getUserId())
                            .splitAmount(splitAmount)
                            .build();
                })
                .toList();

        int result = participantMapper.insertParticipants(participantVOList);
        if(result != participantCount) {
            log.error("참여자 저장 실패");
            throw new RuntimeException("결제 참여자 저장 실패");
        }

        int totalSplit = participantVOList.stream()
                .mapToInt(ParticipantVO::getSplitAmount)
                .sum();
        if(totalSplit != paymentDTO.getAmount()) {
            throw new RuntimeException("분배 금액 총합 오류");
        }
    }
}
