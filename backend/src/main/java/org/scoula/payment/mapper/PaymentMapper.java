package org.scoula.payment.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.scoula.payment.domain.PaymentVO;

@Mapper
public interface PaymentMapper {

    //결제 내역 저장
    int insertPayment(PaymentVO paymentVO);
    
}
