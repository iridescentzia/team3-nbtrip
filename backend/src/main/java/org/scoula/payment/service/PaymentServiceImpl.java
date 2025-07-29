package org.scoula.payment.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.scoula.account.service.AccountService;
import org.scoula.merchant.service.MerchantService;
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
    private final AccountService accountService;
    private final MerchantService merchantService;

    // QR 결제 처리
    @Override
    @Transactional
    public void processPayment(PaymentDTO paymentDTO) {
        if(paymentDTO.getAmount() <= 0) {
            throw new IllegalArgumentException("결제 금액은 0보다 커야 합니다.");
        }

        // 결제 전 잔액 확인
        int beforeBalance = accountService.getBalanceByUserId(paymentDTO.getUserId());

        // 사용자 계좌 잔액 차감
        accountService.decreaseUserBalance(paymentDTO.getUserId(), paymentDTO.getAmount());;

        // 결제 후 잔액 확인
        int afterBalance = accountService.getBalanceByUserId(paymentDTO.getUserId());
        if(afterBalance != beforeBalance - paymentDTO.getAmount()) {
            throw new RuntimeException("결제 금액 오류");
        }

        // 사업자 매출 증가
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
            throw new RuntimeException("결제 오류");
        }
    }
}
