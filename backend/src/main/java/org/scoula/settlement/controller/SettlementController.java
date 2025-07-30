package org.scoula.settlement.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.scoula.settlement.exception.domain.SettlementVO;
import org.scoula.settlement.dto.SettlementDTO;
import org.scoula.settlement.service.SettlementService;
import org.springframework.http.ResponseEntity;
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

    // private final NotificationService notificationService;
    // private final GroupService groupService;
  
    /**
     * 정산 1단계: 정산 요약 정보 조회 API
     * 특정 여행의 총 사용 금액과 멤버별 총 결제 금액을 조회함.
     *
     * @param tripId 조회할 여행의 ID
     * @return ResponseEntity<SettlementSummaryResponseDto> 정산 요약 정보 DTO를 담은 응답
     */
    // FIX: API 경로를 프론트엔드 요청과 일치하도록 수정.
    @GetMapping("/{tripId}/summary")
    public ResponseEntity<SettlementDTO.SettlementSummaryResponseDto> getSettlementSummary(
            @PathVariable int tripId,
            Principal principal
    ) {
        int userId = extractUserId(principal);

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
    // ==================== 조회 API ====================

    /**
     * 1. 여행별 정산 결과 조회 (그룹장용 - 시나리오 2번)
     * 그룹장이 "정산요청하기 페이지"에서 모든 그룹원 간 정산 관계 리스트 전체를 봄
     */
    @GetMapping("/{tripId}")
    public ResponseEntity<List<SettlementVO>> getSettlementsByTripId(@PathVariable("tripId") int tripId) {
        log.info("🟢GET /api/settlements/tripId={}", tripId);
        return ResponseEntity.ok(settlementService.getSettlementsByTripId(tripId));
    }

    /**
     * 2. 개인별 정산 내역 조회 (시나리오 4번)
     * 그룹원 각자 화면에서는 자기 관련 정산만 표시 (내가 누구에게 보낼지/받을지)
     */
    @GetMapping("/my/{tripId}")
    public ResponseEntity<SettlementDTO.PersonalSettlementResponseDto> getMySettlements(
            @PathVariable("tripId") int tripId,
            Principal principal
    ) {
        log.info("🟢GET /api/settlements/my/tripId={}", tripId);
        int userId = extractUserId(principal);
        // int userId = 5; // 앨리스 - 임시 데이터
        return ResponseEntity.ok(settlementService.getMySettlements(userId, tripId));
    }

    /**
     * 3. 사용자의 전체 정산 상태 조회 (시나리오 6,7번)
     */
    @GetMapping("/status")
    public ResponseEntity<SettlementDTO.MySettlementStatusResponseDto> getMySettlementStatus(Principal principal) {
        int userId = extractUserId(principal);
        return ResponseEntity.ok(settlementService.getMyOverallSettlementStatus(userId));
    }

    /**
     * 4. 여행별 미정산 존재 여부
     */
    @GetMapping("/{tripId}/remaining")
    public ResponseEntity<SettlementDTO.RemainingSettlementResponseDto> hasRemainingUnsettled(@PathVariable int tripId) {
        log.info("🟢GET /api/settlements/tripId={}/remaining", tripId);
        return ResponseEntity.ok(settlementService.getRemainingSettlements(tripId));
    }

    // ==================== 정산 요청 생성 API ====================

    /**
     * 5. 정산 요청 생성 (시나리오 3번)
     * 그룹장이 '정산 요청하기' 클릭 시 호출
     * n빵 계산 서비스 호출 -> settlement에 pending 저장
     */
    @PostMapping("")
    public ResponseEntity<SettlementDTO.CreateSettlementResponseDto> createSettlementRequest(
            @RequestBody SettlementDTO.CreateSettlementRequestDto request,
            Principal principal
    ) {
        log.info("🟢POST /api/settlements - create settlement request for tripId={}", request.getTripId());
        int userId = extractUserId(principal);
        int tripId = request.getTripId();

        SettlementDTO.CreateSettlementResponseDto response = settlementService.createSettlementRequest(userId, tripId);

        if (!response.isSuccess()) {
            return ResponseEntity.badRequest().body(response);
        }

        // TODO: 팀원 머지 후 주석 해제 - 그룹원에게 정산 요청 알림 발송
        // try {
        //     sendSettlementNotification(userId, tripId, "SETTLEMENT");
        // } catch (Exception e) {
        //     log.warn("정산 요청 알림 발송 실패", e);
        // }

        return ResponseEntity.ok(response);
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
            Principal principal
    ) {
        String status = request.getStatus();
        log.info("🟢PUT /api/settlements/settlementId={}, status={}", settlementId, status);

        if (!isValidStatus(status)) {
            return ResponseEntity.badRequest().body("Invalid status.");
        }

        int loginUserId = extractUserId(principal);

        // sender 권한 체크
        if (!isSender(settlementId, loginUserId)) {
            log.warn("🟢권한 없음 - userId={} tried to update settlement {}", loginUserId, settlementId);
            return ResponseEntity.status(403).body("권한 없음: sender만 상태 변경 가능");
        }

        int updated = settlementService.updateSettlementStatus(settlementId, status.toUpperCase());
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
            Principal principal
    ) {
        int loginUserId = extractUserId(principal);
        SettlementDTO.TransferResponseDto response = settlementService.transferToUsers(request.getSettlementIds(), loginUserId);

        return response.isSuccess() ? ResponseEntity.ok(response) : ResponseEntity.badRequest().body(response);
    }

    // ==================== 내부 헬퍼 메서드들 ====================

    /**
     * 정산 관련 알림 발송
     */
    private void sendSettlementNotification(int fromUserId, int tripId, String notificationType) {
        // TODO: 팀원 머지 후 주석 해제
        // try {
        //     // 그룹 멤버 조회 및 알림 발송 (자신 제외)
        //     List<Integer> memberIds = groupService.getGroupMembers(tripId).stream()
        //             .map(GroupMemberDTO::getUserId)
        //             .filter(id -> !id.equals(fromUserId))
        //             .collect(Collectors.toList());
        //
        //     for (int memberId : memberIds) {
        //         NotificationDTO dto = NotificationDTO.builder()
        //                 .userId(memberId)
        //                 .fromUserId(fromUserId)
        //                 .tripId(tripId)
        //                 .notificationType(notificationType)
        //                 .build();
        //         notificationService.createNotification(dto);
        //     }
        //
        //     log.info("알림 발송 완료 - tripId: {}, type: {}", tripId, notificationType);
        // } catch (Exception e) {
        //     log.error("알림 발송 실패", e);
        // }

        // 임시: 머지 전까지는 로그만 출력
        log.info("알림 발송 예정 (머지 후 활성화) - tripId: {}, type: {}", tripId, notificationType);
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
     * 로그인 사용자 ID 추출
     */
    private int extractUserId(Principal principal) {
        // TODO: Security와 연동 후 정식으로 교체
        // if(principal == null) {
        //     log.warn("Principal is null");
        //     return 1;
        // }
        // try {
        //     return Integer.parseInt(principal.getName());
        // } catch (Exception e) {
        //     log.warn("Principal parse 실패 [{}]", principal.getName());
        //     return 1;
        // }
        return 7; // 테스트용
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
