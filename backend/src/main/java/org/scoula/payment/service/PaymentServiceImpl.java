package org.scoula.payment.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.scoula.account.service.AccountService;
import org.scoula.payment.domain.PaymentType;
import org.scoula.payment.domain.PaymentVO;
import org.scoula.payment.dto.PaymentDTO;
import org.scoula.payment.mapper.PaymentMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Log4j2
public class PaymentServiceImpl implements PaymentService {

    private final PaymentMapper paymentMapper;
    private final AccountService accountService;

    @Override
    @Transactional
    public void processPayment(PaymentDTO paymentDTO) {
        if(paymentDTO.getAmount() <= 0) {
            log.error("잘못된 결제 금액 입력: {}", paymentDTO.getAmount());
            throw new IllegalArgumentException("결제 금액은 0보다 커야 합니다.");
        }

        log.info("[결제 시작] 사용자 ID: {}, 금액: {}", paymentDTO.getUserId(), paymentDTO.getAmount());

        // 사용자 계좌에서 잔액 차감
        boolean decreased = accountService.decreaseUserBalance(paymentDTO.getUserId(), paymentDTO.getAmount());
        if(!decreased) {
            log.error("사용자 계좌 잔액 차감 실패 - 사용자 ID: {}", paymentDTO.getUserId());
            throw new RuntimeException("결제 실패: 존재하지 않는 사용자이거나 잔액 부족");
        }

//        // 가맹점 계좌에 금액 증가
//        boolean increased = accountService.increaseMerchantBalance(paymentDTO.getMerchantId(), paymentDTO.getAmount());
//        if(!increased) {
//            log.error("가맹점 정산 실패 - 가맹점 ID: {}", paymentDTO.getMerchantId());
//            throw new RuntimeException("가맹점 정산 실패");
//        }

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
            throw new RuntimeException("결제 내역 저장 실패");
        }

        log.info("[결제 완료]: {}", paymentVO);
    }
}
