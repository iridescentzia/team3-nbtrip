package org.scoula.paymentlist.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.scoula.merchant.dto.MerchantDTO;
import org.scoula.paymentlist.service.PaymentListService;
import org.scoula.report.dto.ChartDTO;
import org.scoula.report.service.ChartService;
import org.scoula.security.accounting.domain.CustomUser;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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

    /** 결제내역 전체 조회**/
    @GetMapping("/{tripId}")
    public ResponseEntity<Map<String, Object>> getPaymentList(
            @PathVariable int tripId,
            @AuthenticationPrincipal CustomUser customUser
            ) {
        if(customUser == null) {
            return ResponseEntity.status(401).body(Map.of("error", "로그인이 필요합니다."));
        }
        Integer userId = customUser.getUserId();
        log.info("결제 내역 요청 - tripId={}, userId={}", tripId, userId);

        Map<String, Object> response = new HashMap<>();
        response.put("paymentData", paymentService.getPaymentList(tripId, userId));
        log.info("api 불러오는중");
        return ResponseEntity.ok(response);
    }

    // 결제 단건 조회
    @GetMapping("/payment/{paymentId}")
    public ResponseEntity<Map<String, Object>> getPaymentById(
            @PathVariable int paymentId,
            @AuthenticationPrincipal CustomUser customUser
    ) {
        if (customUser == null) {
            return ResponseEntity.status(401).body(Map.of("error", "로그인이 필요합니다."));
        }

        Integer userId = customUser.getUserId();
        log.info("결제 상세 요청 - paymentId={}, userId={}", paymentId, userId);

        var payment = paymentService.getPaymentListByPaymentId(paymentId, userId); // 👉 변수에 담기

        if (payment == null) {
            return ResponseEntity.status(404).body(Map.of("error", "해당 결제를 찾을 수 없습니다."));
        }

        Map<String, Object> response = new HashMap<>();
        response.put("payment", payment);
        return ResponseEntity.ok(response);
    }
}
