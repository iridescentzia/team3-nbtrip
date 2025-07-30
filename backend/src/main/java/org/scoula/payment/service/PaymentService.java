package org.scoula.payment.service;

import org.scoula.payment.dto.PaymentDTO;

public interface PaymentService {
    // QR 결제 프로세스
    void processPayment(PaymentDTO paymentDTO);
}
