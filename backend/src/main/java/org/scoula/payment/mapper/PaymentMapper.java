package org.scoula.payment.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.scoula.payment.domain.PaymentVO;

@Mapper
public interface PaymentMapper {

    //거래 생성
    int insertPayment(PaymentVO payment);

    //결제자 계좌 잔액 차감
    int decreaseUserBalance(PaymentVO payment);

    //가맹점 사업자 계좌로 송금
    int increaseMerchantBalance(PaymentVO payment);
    
}
