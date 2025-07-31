package org.scoula.payment.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.scoula.payment.dto.PaymentDTO;
import org.scoula.payment.service.PaymentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
@Log4j2
public class PaymentController {

    private final PaymentService paymentService;

    // QR 결제
    @PostMapping("/qr")
    public ResponseEntity<String> processQrPayment(@RequestBody PaymentDTO paymentDTO) {
        try {
            paymentService.processPayment(paymentDTO);
            return ResponseEntity.ok("결제 완료!");
        } catch (RuntimeException e) {
            log.error("QR 결제 실패: {}", e.getMessage());
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(e.getMessage());
        }
    }
}
