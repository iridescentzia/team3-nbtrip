package org.scoula.payment.service;

import org.scoula.payment.dto.PaymentDTO;

public interface PaymentService {
    // QR 결제 프로세스
    void processPayment(PaymentDTO paymentDTO, int userId, int tripId);

    // 선결제/기타 결제 수동 등록
    void registerManualPayment(PaymentDTO paymentDTO, int userId, int tripId);
}
