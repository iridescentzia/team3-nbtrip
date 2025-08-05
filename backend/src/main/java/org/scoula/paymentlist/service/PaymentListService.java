package org.scoula.paymentlist.service;

import org.apache.ibatis.annotations.Param;
import org.scoula.paymentlist.dto.PaymentListDTO;

import java.util.List;

public interface PaymentListService {
    List<PaymentListDTO> getPaymentList(Integer tripId);
    PaymentListDTO getPaymentListByPaymentId(Integer paymentId);

}

