package org.scoula.settlement.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.scoula.notification.dto.NotificationDTO;
import org.scoula.notification.service.NotificationService;
import org.scoula.settlement.domain.SettlementVO;
import org.scoula.settlement.dto.SettlementDTO;
import org.scoula.settlement.service.SettlementService;
import org.scoula.security.accounting.domain.CustomUser;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/api/settlements")
@RequiredArgsConstructor
@Log4j2
public class SettlementController {
    private final SettlementService settlementService;
    private final NotificationService notificationService;  // 팀원 알림 서비스 주입

    /**
     * 정산 1단계: 정산 요약 정보 조회 API
     * 특정 여행의 총 사용 금액과 멤버별 총 결제 금액을 조회함.
     *
     * @param tripId 조회할 여행의 ID
     * @return ResponseEntity<SettlementSummaryResponseDto> 정산 요약 정보 DTO를 담은 응답
     */
    @GetMapping("/{tripId}/summary")
    public ResponseEntity<SettlementDTO.SettlementSummaryResponseDto> getSettlementSummary(
            @PathVariable int tripId,
            @AuthenticationPrincipal CustomUser customUser
    ) {
        Integer userId = customUser.getUserId();
        log.info("정산 요약 조회: userId={}, tripId={}", userId, tripId);

        // 그룹장 권한 체크
        if(!settlementService.canRequestSettlement(userId, tripId)) {
            return ResponseEntity.status(403).body(null);
        }

        // SettlementService를 호출하여 비즈니스 로직을 수행
        SettlementDTO.SettlementSummaryResponseDto summaryDto = settlementService.getSettlementSummary(tripId);

        // 조회된 데이터를 ResponseEntity에 담아 프론트엔드로 반환
        return ResponseEntity.ok(summaryDto);
    }

    /**
     * [NEW] 정산 2단계: 최종 정산 결과 계산 API
     * 상계 처리가 완료된 최적의 송금 목록을 조회함.
     *
     * @param tripId 조회할 여행의 ID
     * @return ResponseEntity<SettlementResultResponseDto> 최종 송금 목록을 담은 DTO
     */
    @GetMapping("/{tripId}/calculate")
    public ResponseEntity<SettlementDTO.SettlementResultResponseDto> calculateFinalSettlement(@PathVariable int tripId) {
        // SettlementService를 호출하여 최종 정산 결과를 계산
        SettlementDTO.SettlementResultResponseDto resultDto = settlementService.calculateFinalSettlement(tripId);

        // 계산된 결과를 ResponseEntity에 담아 프론트엔드로 반환
        return ResponseEntity.ok(resultDto);
    }
    // ==================== 정산 영수증 API ====================

    /**
     * 멤버별 정산 영수증(정산 과정) 상세 내역 조회 API
     *
     * @param tripId 조회할 여행의 ID
     * @param otherUserId 상대방 사용자의 ID
     * @param customUser 현재 로그인한 사용자 정보
     * @return ResponseEntity<SettlementBreakdownResponseDto> 두 사용자 간의 정산 과정 상세 내역
     */
    @GetMapping("/{tripId}/breakdown/{otherUserId}")
    public ResponseEntity<SettlementDTO.SettlementBreakdownResponseDto> getSettlementBreakdown(
            @PathVariable("tripId") int tripId,
            @PathVariable("otherUserId") int otherUserId,
            @AuthenticationPrincipal CustomUser customUser) {

        // 1. @AuthenticationPrincipal을 통해 현재 로그인한 사용자의 ID를 안전하게 가져옵니다.
        int myUserId = customUser.getUserId();

        // 2. Service에 필요한 모든 ID를 전달하여 비즈니스 로직을 수행합니다.
        SettlementDTO.SettlementBreakdownResponseDto breakdownDto = settlementService.getSettlementBreakdown(tripId, myUserId, otherUserId);

        // 3. 조회된 데이터를 ResponseEntity에 담아 프론트엔드로 반환합니다.
        return ResponseEntity.ok(breakdownDto);
    }

    // ==================== 조회 API ====================

    /**
     * 1. 여행별 정산 결과 조회 (그룹장용 - 시나리오 2번)
     * 그룹장이 "정산요청하기 페이지"에서 모든 그룹원 간 정산 관계 리스트 전체를 봄
     */
    @GetMapping("/{tripId}")
    public ResponseEntity<List<SettlementVO>> getSettlementsByTripId(
            @PathVariable("tripId") int tripId,
            @AuthenticationPrincipal CustomUser customUser
    ) {
        Integer userId = customUser.getUserId();
        log.info("🟢GET /api/settlements/tripId={}, userId={}", tripId, userId);
        return ResponseEntity.ok(settlementService.getSettlementsByTripId(tripId));
    }

    /**
     * 2. 개인별 정산 내역 조회 (시나리오 4번)
     * 그룹원 각자 화면에서는 자기 관련 정산만 표시 (내가 누구에게 보낼지/받을지)
     */
    @GetMapping("/my/{tripId}")
    public ResponseEntity<SettlementDTO.PersonalSettlementResponseDto> getMySettlements(
            @PathVariable("tripId") int tripId,
            @AuthenticationPrincipal CustomUser customUser
    ) {
        Integer userId = customUser.getUserId();
        log.info("🟢GET /api/settlements/my/tripId={}, userId={}", tripId, userId);
        return ResponseEntity.ok(settlementService.getMySettlements(userId, tripId));
    }

    /**
     * 3. 사용자의 전체 정산 상태 조회 (시나리오 6,7번)
     */
    @GetMapping("/status")
    public ResponseEntity<SettlementDTO.MySettlementStatusResponseDto> getMySettlementStatus(
            @AuthenticationPrincipal CustomUser customUser
    ) {
        Integer userId = customUser.getUserId();
        log.info("정산 상태 조회: userId={}", userId);
        return ResponseEntity.ok(settlementService.getMyOverallSettlementStatus(userId));
    }

    /**
     * 4. 여행별 미정산 존재 여부
     */
    @GetMapping("/{tripId}/remaining")
    public ResponseEntity<SettlementDTO.RemainingSettlementResponseDto> hasRemainingUnsettled(
            @PathVariable int tripId,
            @AuthenticationPrincipal CustomUser customUser
    ) {
        Integer userId = customUser.getUserId();
        log.info("🟢GET /api/settlements/tripId={}/remaining, userId={}", tripId, userId);
        return ResponseEntity.ok(settlementService.getRemainingSettlements(tripId));
    }

    // ==================== 정산 요청 생성 API ====================

    /**
     * 5. 정산 요청 생성 (시나리오 3번)
     * 그룹장이 '정산 요청하기' 클릭 시 호출
     * n빵 계산 서비스 호출 -> settlement에 pending 저장 (알림 발송 X)
     */
    @PostMapping("")
    public ResponseEntity<SettlementDTO.CreateSettlementResponseDto> createSettlementRequest(
            @RequestBody SettlementDTO.CreateSettlementRequestDto request,
            @AuthenticationPrincipal CustomUser customUser
    ) {
        Integer userId = customUser.getUserId();
        int tripId = request.getTripId();
        log.info("🟢POST /api/settlements - create settlement request for tripId={}, userId={}", tripId, userId);

        SettlementDTO.CreateSettlementResponseDto response = settlementService.createSettlementRequest(userId, tripId);

        if (!response.isSuccess()) {
            return ResponseEntity.badRequest().body(response);
        }

        // 정산 데이터만 생성, 알림은 별도 API에서 처리
        log.info("정산 요청 생성 완료 (알림 발송 없음) - tripId: {}, fromUserId: {}", tripId, userId);
        return ResponseEntity.ok(response);
    }

    /**
     * 정산 요청 알림 발송 API
     * 프론트엔드에서 "정산요청하기" 버튼 클릭 시 호출
     */
    @PostMapping("/{tripId}/notify")
    public ResponseEntity<String> sendSettlementRequestNotification(
            @PathVariable int tripId,
            @AuthenticationPrincipal CustomUser customUser
    ) {
        Integer userId = customUser.getUserId();
        log.info("🟢POST /api/settlements/{}/notify - 정산 요청 알림 발송: userId={}", tripId, userId);

        try {
            // 정산 요청 알림 발송
            sendSettlementNotification(userId, tripId, "SETTLEMENT");

            return ResponseEntity.ok("정산 요청 알림이 발송되었습니다.");
        } catch (Exception e) {
            log.error("정산 요청 알림 발송 실패 - tripId: {}, userId: {}, error: {}", tripId, userId, e.getMessage());
            return ResponseEntity.status(500).body("알림 발송에 실패했습니다.");
        }
    }

    // ==================== 상태 업데이트 API ====================

    /**
     * 6. 정산 상태 업데이트 (시나리오 5번)
     * 특정 정산 row(1건)의 상태 변경
     * ex) PUT /api/settlements/12/status → A가 B에게 송금 상태 변경
     */
    @PutMapping("/{settlementId}/status")
    public ResponseEntity<String> updateStatus(
            @PathVariable int settlementId,
            @RequestBody SettlementDTO.UpdateSettlementStatusRequestDto request,
            @AuthenticationPrincipal CustomUser customUser
    ) {
        Integer loginUserId = customUser.getUserId();
        String status = request.getStatus();
        log.info("🟢PUT /api/settlements/settlementId={}, status={}, userId={}", settlementId, status, loginUserId);

        if (!isValidStatus(status)) {
            return ResponseEntity.badRequest().body("Invalid status.");
        }

        // sender 권한 체크
        if (!isSender(settlementId, loginUserId)) {
            log.warn("🟢권한 없음 - userId={} tried to update settlement {}", loginUserId, settlementId);
            return ResponseEntity.status(403).body("권한 없음: sender만 상태 변경 가능");
        }

        int updated = settlementService.updateSettlementStatus(settlementId, status.toUpperCase());

        // 전체 정산 완료 시에만 완료 알림 발송
        if (updated == 1 && "COMPLETED".equals(status.toUpperCase())) {
            try {
                SettlementVO settlement = settlementService.getById(settlementId);

                // 해당 여행의 모든 정산이 완료되었는지 확인
                if (settlementService.isAllSettlementCompleted(settlement.getTripId())) {
                    sendSettlementCompletedNotification(loginUserId, settlement.getTripId());
                    log.info("🎉 전체 정산 완료 알림 발송 완료 - tripId: {}", settlement.getTripId());
                } else {
                    log.info("✅ 개별 정산 완료, 전체 정산은 아직 진행 중 - settlementId: {}", settlementId);
                }
            } catch (Exception e) {
                log.warn("정산 완료 알림 발송 실패 - settlementId: {}, error: {}", settlementId, e.getMessage());
            }
        }

        return (updated == 1)
                ? ResponseEntity.ok("Status changed to " + status)
                : ResponseEntity.badRequest().body("Update failed. Invalid settlementId");
    }

    // ==================== 송금 처리 API ====================

    /**
     * 7. 실제 그룹원 간 송금 처리 (시나리오 5번 첫번째 단계)
     * 정산 상태: PENDING -> COMPLETED 전환 + 잔액 차감/입금 수행
     */
    @PostMapping("/transfer")
    public ResponseEntity<SettlementDTO.TransferResponseDto> transferToUsers(
            @RequestBody SettlementDTO.TransferRequestDto request,
            @AuthenticationPrincipal CustomUser customUser
    ) {
        Integer loginUserId = customUser.getUserId();
        log.info("송금 처리 요청: userId={}, settlementIds={}", loginUserId, request.getSettlementIds());

        SettlementDTO.TransferResponseDto response = settlementService.transferToUsers(request.getSettlementIds(), loginUserId);

        // 송금 완료 후 전체 정산 완료 체크 (추가)
        if (response.isSuccess() && !request.getSettlementIds().isEmpty()) {
            try {
                // 첫 번째 정산 건으로 tripId 조회
                SettlementVO firstSettlement = settlementService.getById(request.getSettlementIds().get(0));

                // 송금 완료 알림(SEND) 발송
                NotificationDTO dto = NotificationDTO.builder()
                        .fromUserId(loginUserId)
                        .fromUserNickname(customUser.getNickname())
                        .tripId(firstSettlement.getTripId())
                        .notificationType("SEND") // 서비스에서 SETTLEMENT/SEND로 처리됨
                        .build();
                notificationService.createNotification(dto);

                // 전체 정산 완료 확인
                if (settlementService.isAllSettlementCompleted(firstSettlement.getTripId())) {
                    sendSettlementCompletedNotification(loginUserId, firstSettlement.getTripId());
                    log.info("🎉 송금 완료 후 전체 정산 완료 알림 발송 - tripId: {}", firstSettlement.getTripId());
                }
            } catch (Exception e) {
                log.warn("송금 완료 후 전체 정산 완료 체크 실패", e);
            }
        }

        return response.isSuccess() ? ResponseEntity.ok(response) : ResponseEntity.badRequest().body(response);
    }

    @GetMapping("/unsettled-trips")
    public ResponseEntity<List<SettlementDTO.UnsettledTripInfo>> getMyUnsettledTrips(@AuthenticationPrincipal CustomUser customUser) {
        // 1. Principal 객체에서 현재 로그인한 사용자의 ID를 추출합니다.
        //    (실제 구현 시에는 JWT 토큰에 저장된 ID를 사용하게 됩니다.)
        // int userId = Integer.parseInt(principal.getName());
        int userId = customUser.getUserId(); // 테스트를 위해 임시로 '김민수(ID:1)' 사용자로 가정합니다.

        // 2. Service 계층에 userId를 전달하여 비즈니스 로직을 수행합니다.
        List<SettlementDTO.UnsettledTripInfo> unsettledTrips = settlementService.getUnsettledTrips(userId);

        // 3. 조회된 데이터를 ResponseEntity에 담아 프론트엔드로 반환합니다.
        return ResponseEntity.ok(unsettledTrips);
    }

    // ==================== 내부 헬퍼 메서드들 ====================

    /**
     * 정산 요청 알림 발송 (NotificationService 활용)
     */
    private void sendSettlementNotification(int fromUserId, int tripId, String notificationType) {
        try {
            NotificationDTO dto = NotificationDTO.builder()
                    .fromUserId(fromUserId)
                    .tripId(tripId)
                    .notificationType(notificationType)  // "SETTLEMENT"
                    .isRead(false)
                    .build();

            // NotificationService가 자동으로 다음을 처리:
            // 1. trip 멤버 전체 조회 (mapper.findUserIdsByTripId)
            // 2. DB에 알림 저장 (mapper.createSettlementNotificationForAll)
            // 3. 각 멤버에게 FCM 푸시 발송 ("정산 요청이 도착했어요")
            notificationService.createNotification(dto);

            log.info("정산 요청 알림 발송 완료 - fromUserId: {}, tripId: {}", fromUserId, tripId);
        } catch (Exception e) {
            log.error("정산 요청 알림 발송 실패 - fromUserId: {}, tripId: {}, error: {}",
                    fromUserId, tripId, e.getMessage());
            throw e;
        }
    }

    /**
     * 정산 완료 알림 발송 (NotificationService 활용)
     */
    private void sendSettlementCompletedNotification(int fromUserId, int tripId) {
        try {
            NotificationDTO dto = NotificationDTO.builder()
                    .fromUserId(fromUserId)
                    .tripId(tripId)
                    .notificationType("COMPLETED")  // "COMPLETED"
                    .isRead(false)
                    .build();

            // NotificationService가 자동으로 다음을 처리:
            // 1. trip 멤버 전체 조회 (mapper.findUserIdsByTripId)
            // 2. DB에 알림 저장 (mapper.createCompletedNotification)
            // 3. 각 멤버에게 FCM 푸시 발송 ("정산이 완료되었어요")
            notificationService.createNotification(dto);

            log.info("정산 완료 알림 발송 완료 - fromUserId: {}, tripId: {}", fromUserId, tripId);
        } catch (Exception e) {
            log.error("정산 완료 알림 발송 실패 - fromUserId: {}, tripId: {}, error: {}",
                    fromUserId, tripId, e.getMessage());
            throw e;
        }
    }

    /**
     * 상태 값 유효성 검증
     */
    private boolean isValidStatus(String status) {
        if (status == null) return false;
        switch (status.toUpperCase()) {
            case "PENDING":
            case "PROCESSING":
            case "COMPLETED":
                return true;
            default:
                return false;
        }
    }

    /**
     * 주어진 settlementId의 sender가 로그인 사용자와 같은지 확인
     */
    private boolean isSender(int settlementId, int loginUserId) {
        try {
            SettlementVO vo = settlementService.getById(settlementId);
            Integer sender = vo.getSenderId();
            return sender != null && sender.equals(loginUserId);
        } catch (NoSuchElementException e) {
            log.warn("settlementId={} 찾을 수 없음", settlementId);
            return false;
        }
    }


}