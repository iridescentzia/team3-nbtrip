package org.scoula.paymentlist.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.scoula.merchant.dto.MerchantDTO;
import org.scoula.paymentlist.service.PaymentListService;
import org.scoula.report.dto.ChartDTO;
import org.scoula.report.service.ChartService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Log4j2
@RestController
@RequestMapping("/api/paymentlist")
@RequiredArgsConstructor
public class PaymentListController {
    private final PaymentListService paymentService;

    /** 결제내역 **/
    @GetMapping("/{tripId}")
    public ResponseEntity<Map<String, Object>> getPaymentList(@PathVariable int tripId) {
        Map<String, Object> response = new HashMap<>();
        response.put("paymentData", paymentService.getPaymentList(tripId));
        log.info("api 불러오는중");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/payment/{paymentId}")
    public ResponseEntity<Map<String, Object>> getPaymentById(@PathVariable int paymentId) {
        Map<String, Object> response = new HashMap<>();
        response.put("payment", paymentService.getPaymentListByPaymentId(paymentId));
        return ResponseEntity.ok(response);
    }
}
