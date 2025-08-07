package org.scoula.payment.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.scoula.payment.domain.PaymentVO;

@Mapper
public interface PaymentMapper {

    //결제 내역 저장
    int insertPayment(PaymentVO paymentVO);

    // 결제 ID 기준 단건 조회
    PaymentVO selectPaymentById(@Param("paymentId") int paymentId);

    /* 결제 상세 수정 */
    // 메모 수정
    int updateMemo(@Param("paymentId") int paymentId, @Param("memo") String memo);

    // 선결제/기타 결제 상세 수정
    int updateManualPayment(PaymentVO paymentVO);

    // 결제 내역 삭제
    int deletePaymentByPaymentId(@Param("paymentId") int paymentId);
}
