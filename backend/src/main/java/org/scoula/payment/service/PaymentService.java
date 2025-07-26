package org.scoula.payment.service;

import org.scoula.payment.dto.PaymentDTO;

public interface PaymentService {
    void processPayment(PaymentDTO paymentDTO);
}
