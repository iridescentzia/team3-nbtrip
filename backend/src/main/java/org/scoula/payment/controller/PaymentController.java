package org.scoula.payment.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.scoula.payment.domain.ParticipantVO;
import org.scoula.payment.domain.PaymentType;
import org.scoula.payment.dto.PaymentDTO;
import org.scoula.payment.service.PaymentService;
import org.scoula.security.accounting.domain.CustomUser;
import org.scoula.trip.domain.TripStatus;
import org.scoula.trip.dto.TripDTO;
import org.scoula.trip.service.TripService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
@Log4j2
public class PaymentController {

    private final PaymentService paymentService;
    private final TripService tripService;

    // QR 결제
    @PostMapping("/qr")
    public ResponseEntity<String> processQrPayment(@RequestBody PaymentDTO paymentDTO, @AuthenticationPrincipal CustomUser customUser) {
        Integer userId = customUser.getUserId();
        try {
            List<TripDTO> trips = tripService.getJoinedTrips(userId);
            TripDTO currentTrip = new TripDTO();
            for(TripDTO trip : trips) {
                if ((trip.getStartDate().isBefore(LocalDate.now()) || trip.getStartDate().isEqual(LocalDate.now())) &&
                        (trip.getEndDate().isAfter(LocalDate.now()) || trip.getEndDate().isEqual(LocalDate.now())) &&
                        trip.getTripStatus() == TripStatus.ACTIVE) {
                    currentTrip = trip;
                }
            }
            int tripId = currentTrip.getTripId();
            paymentService.processPayment(paymentDTO, userId, tripId); //userId, tripId, paymentDTO 매개변수로 받도록
            return ResponseEntity.ok("결제 완료!");
        } catch (RuntimeException e) {
            log.error("QR 결제 실패: {}", e.getMessage());
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(e.getMessage());
        }
    }

    // 선결제 등록
    @PostMapping("/prepaid")
    public ResponseEntity<String> registerPrepaidPayment(@RequestBody PaymentDTO paymentDTO, @AuthenticationPrincipal CustomUser customUser) {
        int userId = paymentDTO.getPayerId();
        try {
            int tripId = paymentDTO.getTripId();

            paymentDTO.setPaymentType(org.scoula.payment.domain.PaymentType.PREPAID);
            paymentService.registerManualPayment(paymentDTO, userId, tripId);

            return ResponseEntity.ok("선결제 등록 완료");
        } catch(RuntimeException e) {
            log.error("선결제 등록 실패: {}", e.getMessage());
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(e.getMessage());
        }
    }

    // 기타 결제 등록
    @PostMapping("/other")
    public ResponseEntity<String> registerOtherPayment(@RequestBody PaymentDTO paymentDTO, @AuthenticationPrincipal CustomUser customUser) {
        int userId = paymentDTO.getPayerId();
        try {
            int tripId = paymentDTO.getTripId();

            paymentDTO.setPaymentType(org.scoula.payment.domain.PaymentType.OTHER);
            paymentService.registerManualPayment(paymentDTO, userId, tripId);

            return ResponseEntity.ok("기타 결제 등록 완료");
        } catch(RuntimeException e) {
            log.error("기타 결제 등록 실패: {}", e.getMessage());
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(e.getMessage());
        }
    }

    // QR 결제 수정
    @PutMapping("/qr/{paymentId}")
    public ResponseEntity<String> updateQrPayment(
            @PathVariable int paymentId,
            @RequestBody PaymentDTO paymentDTO,
            @AuthenticationPrincipal CustomUser customUser) {
        int userId = paymentDTO.getPayerId();
        paymentDTO.setPaymentType(PaymentType.QR);
        try {
            paymentService.updateQrPayment(paymentId, paymentDTO, userId);
            return ResponseEntity.ok("QR 결제 수정 완료");
        } catch (RuntimeException e) {
            log.error("QR 결제 수정 실패: {}", e.getMessage());
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(e.getMessage());
        }
    }

    // 선결제 수정
    @PutMapping("/prepaid/{paymentId}")
    public ResponseEntity<String> updatePrepaidPayment(
            @PathVariable int paymentId,
            @RequestBody PaymentDTO paymentDTO,
            @AuthenticationPrincipal CustomUser customUser) {
        int userId = paymentDTO.getPayerId();
        paymentDTO.setPaymentType(PaymentType.PREPAID);
        try {
            paymentService.updateManualPayment(paymentId, paymentDTO, userId);
            return ResponseEntity.ok("선결제 수정 완료");
        } catch (RuntimeException e) {
            log.error("선결제 수정 실패: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    // 기타 결제 수정
    @PutMapping("/other/{paymentId}")
    public ResponseEntity<String> updateOtherPayment(
            @PathVariable int paymentId,
            @RequestBody PaymentDTO paymentDTO,
            @AuthenticationPrincipal CustomUser customUser) {
        int userId = paymentDTO.getPayerId();
        paymentDTO.setPaymentType(PaymentType.OTHER);
        try {
            paymentService.updateManualPayment(paymentId, paymentDTO, userId);
            return ResponseEntity.ok("기타 결제 수정 완료");
        } catch (RuntimeException e) {
            log.error("기타 결제 수정 실패: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    // 결제 참여자 조회
    @GetMapping("/{paymentId}/participants")
    public ResponseEntity<List<ParticipantVO>> getParticipants(@PathVariable int paymentId) {
        List<ParticipantVO> participants = paymentService.getParticipantsByPaymentId(paymentId);
        return ResponseEntity.ok(participants);
    }

    // 결제 내역 삭제
    @DeleteMapping("/{paymentId}")
    public ResponseEntity<String> deletePayment(
            @PathVariable int paymentId,
            @AuthenticationPrincipal CustomUser customUser
    ) {
        try {
            Integer userId = customUser.getUserId();
            paymentService.deletePayment(paymentId, userId);
            return ResponseEntity.ok("결제 삭제 완료");
        } catch (RuntimeException e) {
            log.error("결제 삭제 실패: {}", e.getMessage());
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("결제 삭제 실패: " + e.getMessage());
        }
    }

}
