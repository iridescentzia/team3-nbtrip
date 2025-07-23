package org.scoula.payment.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.scoula.account.mapper.AccountMapper;
import org.scoula.payment.domain.PaymentType;
import org.scoula.payment.domain.PaymentVO;
import org.scoula.payment.dto.PaymentDTO;
import org.scoula.payment.mapper.PaymentMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Log4j2
public class PaymentServiceImpl implements PaymentService {

    private final PaymentMapper paymentMapper;
    private final AccountMapper accountMapper;

    @Override
    @Transactional
    public void processPayment(PaymentDTO paymentDTO) {
        log.info("[결제 시작] 사용자 ID: {}, 금액: {}", paymentDTO.getUserId(), paymentDTO.getAmount());

        // 사용자 계좌에서 잔액 차감
        int decreased = accountMapper.decreaseUserBalance(paymentDTO.getUserId(), paymentDTO.getAmount());
        log.info("[잔액 차감] 결과: {}", decreased);
        if(decreased == 0) {
            throw new RuntimeException("잔액 부족으로 결제 실패");
        }

        // 가맹점 계좌에 금액 증가
        int increased = accountMapper.increaseMerchantBalance(paymentDTO.getMerchantId(), paymentDTO.getAmount());
        log.info("[가맹점 정산] 결과: {}", increased);
        if(increased == 0) {
            throw new RuntimeException("가맹점 정산 실패");
        }

        // 결제 내역 저장
        PaymentVO paymentVO = new PaymentVO();
        paymentVO.setTripId(paymentDTO.getTripId());
        paymentVO.setUserId(paymentDTO.getUserId());
        paymentVO.setMerchantId(paymentDTO.getMerchantId());
        paymentVO.setAmount(paymentDTO.getAmount());
        paymentVO.setPaymentType(PaymentType.QR);
        paymentVO.setPayAt(LocalDateTime.now());

        int inserted = paymentMapper.insertPayment(paymentVO);
        log.info("[결제 내역 저장] 결과: {}", inserted);
        if(inserted == 0) {
            throw new RuntimeException("결제 내역 저장 실패");
        }

        log.info("[결제 완료]: {}", paymentVO);
    }
}
