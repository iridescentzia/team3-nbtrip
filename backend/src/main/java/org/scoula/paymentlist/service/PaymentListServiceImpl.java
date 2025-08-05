package org.scoula.paymentlist.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.scoula.paymentlist.dto.PaymentListDTO;
import org.scoula.paymentlist.mapper.PaymentListMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Log4j2
@Service
@RequiredArgsConstructor
public class PaymentListServiceImpl implements PaymentListService {

    private final PaymentListMapper mapper;

    @Override
    public List<PaymentListDTO> getPaymentList(Integer tripId) {
        return mapper.selectPaymentList(tripId)
                .stream()
                .map(PaymentListDTO::of)
                .toList();
    }

    @Override
    public PaymentListDTO getPaymentListByPaymentId(Integer paymentId) {
        return mapper.selectPaymentById(paymentId)
                .stream()
                .findFirst()
                .map(PaymentListDTO::of)
                .orElse(null);
    }
}