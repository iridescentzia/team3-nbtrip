package org.scoula.payment.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
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
}
