package org.scoula.settlement.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.scoula.settlement.domain.SettlementVO;
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
    // private final SettlementCalculator settlementCalculator; // n빵 계산 서비스
    // private final GroupService groupService; // 그룹장 검증할 때 사용

    // 1. 여행별 정산 결과 조회
    @GetMapping("/{tripId}")
    public ResponseEntity<List<SettlementVO>> getSettlementsByTripId(@PathVariable("tripId") int tripId) {
        log.info("🟢GET /api/settlements/tripId={}", tripId);
        return ResponseEntity.ok(settlementService.getSettlementsByTripId(tripId));
    }

    // 2. 정산 요청 생성 (그룹장만 가능)
    // 프론트 : 그룹장이 '정산 요청하기' 클릭 시 호출
    // 내부 : n빵 계산 서비스 호출 -> settlement에 pending 저장
    @PostMapping("")
    public ResponseEntity<List<SettlementVO>> createSettlementRequest(
            @RequestParam("tripId") int tripId,
            Principal principal // 로그인한 사용자
    ) {
        log.info("🟢POST /api/settlements?tripId={} - create settlement request", tripId);
        // principal에서 로그인 userId 추출 후 그룹장인지 검증
        int userId = extractUserId(principal);

        // 그룹장 권한 체크
        //if(!groupService.isOwner(tripId, userId)) {
        //    log.warn("정산 요청 차단 - userId={}는 tripId={}의 그룹장이 아님", userId, tripId);
        //    return ResponseEntity.status(403).build();
        //}

        // 이미 정산 요청한 적 있는 상태인데 또 요청하는 경우 방어
        if(!settlementService.getPendingOrProcessingByTripId(tripId).isEmpty()) {
            return ResponseEntity.status(409).body(null);
        }

        // n빵 계산 서비스 연동 + settlement에 insert (모두 pending)
        // List<SettlementDTO> results = settlementCalculator.calculate(tripId);
        // settlementService.saveCalculatedResults(results);

        return ResponseEntity.ok(settlementService.getSettlementsByTripId(tripId));
    }

    // 3. 정산 상태 업데이트 (pending -> processing)
    // 특정 정산 row(1건)의 상태 변경
    // ex) /api/settlements/12/status?status=PROCESSING → A가 B에게 송금 시작
    @PutMapping("/{settlementId}/status")
    public ResponseEntity<String> updateStatus(
            @PathVariable int settlementId,
            @RequestParam String status,
            Principal principal
    ) {
        final String next = status == null ? "" : status.toUpperCase();
        log.info("🟢PUT /api/settlements/settlementId={}, status={}", settlementId, next);

        if(!isValidStatus(status)) {
            return ResponseEntity.badRequest().body("Invalid status.");
        }

        int loginUserId = extractUserId(principal);

        // 기본으로 sender만 상태 변경 가능 -> 추후, 조정 가능
        if(!isSender(settlementId, loginUserId)) {
            log.warn("🟢권한 없음 - userId={} tried to update settlement {}", loginUserId, settlementId);
            return ResponseEntity.status(403).body("권한 없음: sender만 상태 변경 가능");
        }

        // sender 권한 체크 (principal -> userId)
        int updated = settlementService.updateSettlementStatus(settlementId, next);
        return (updated == 1)
                ? ResponseEntity.ok("Status changed to " + next)
                : ResponseEntity.badRequest().body("Update failed. Invalid settlementId");
    }

    // 4. 송금 완료 처리
    // 프론트 : 송금 완료 버튼 -> 호출
    // 내부 : 현재 상태가 processing인지 확인 후 completed로 변경
    @PostMapping("/{settlementId}/complete")
    public ResponseEntity<String> completeSettlement(
            @PathVariable int settlementId,
            Principal principal
    ) {
        log.info("🟢POST /api/settlements/settlementId={}/complete", settlementId);

        int loginUserId = extractUserId(principal);

        SettlementVO vo;
        try{
            vo = settlementService.getById(settlementId); // 없으면 예외 던지기
        } catch (NoSuchElementException e) {
            return ResponseEntity.badRequest().body("정산 내역을 찾을 수 없음");
        }

        // sender 권한 체크
        if(vo.getSenderId() == null || vo.getSenderId() != loginUserId) {
            log.warn("권한 없음 - userId={} tried to complete settlement {} (sender={})", loginUserId, settlementId, vo.getSenderId());
            return ResponseEntity.status(403).body("권한 없음 - sender만 완료 처리 가능");
        }

        // 상태 검증 : processing에서만 완료 허용
        if(!"PROCESSING".equalsIgnoreCase(vo.getSettlementStatus())) {
            return ResponseEntity.badRequest().body("processing 상태에서만 완료 가능합니다.");
        }

        boolean ok = settlementService.markAsCompleted(settlementId);
        return ok
                ? ResponseEntity.ok("Settlement marked as COMPLETED.")
                : ResponseEntity.badRequest().body("정산 완료 처리 실패");
    }

    // 5. 사용자의 정산 상태 조회
    @GetMapping("/status")
    public ResponseEntity<String> getMySettlementStatus(Principal principal) {
        int userId = extractUserId(principal);
        String status = settlementService.getMyOverallSettlementStatus(userId);
        return ResponseEntity.ok(status); // "COMPLETED", "PROCESSING", or "PENDING"
    }

    // 6. 여행별 미정산 존재 여부 (메인 홈 "아직 안한 정산" 용)
    // true : 아직 pending/processing 남아 있음
    // false : 모두 completed
    @GetMapping("/{tripId}/remaining")
    public ResponseEntity<Boolean> hasRemainingUnsettled(@PathVariable int tripId) {
        log.info("🟢GET /api/settlements/tripId={}/remaining", tripId);
        boolean hasRemaining = !settlementService.getPendingOrProcessingByTripId(tripId).isEmpty();
        return ResponseEntity.ok(hasRemaining);
    }

    // 7. 실제 그룹원 간 송금 처리 (정산 상태: pending -> processing 전환 + 잔액 차감/입금 수행)
    @PostMapping("/{settlementsId}/transfer")
    public ResponseEntity<String> transferToUser(
            @PathVariable int settlementsId,
            Principal principal
    ) {
        log.info("🟢POST /api/settlements/settlementId={}/transfer", settlementsId);
        int loginUserId = extractUserId(principal);

        SettlementVO vo;
        try{
            vo = settlementService.getById(settlementsId);
        } catch (NoSuchElementException e) {
            return ResponseEntity.badRequest().body("정산 내역을 찾을 수 없습니다.");
        }

        // sender 권한 확인
        if(vo.getSenderId() == null || vo.getSenderId() != loginUserId) {
            log.warn("권한 없음 - userId={} tried to transfer settlement {} (sender={})", loginUserId, settlementsId, vo.getSenderId());
            return ResponseEntity.status(403).body("권한 없음 - 송금은 sender만 할 수 있습니다.");
        }

        // 상태 확인
        if(!"PENDING".equalsIgnoreCase(vo.getSettlementStatus())) {
            return ResponseEntity.badRequest().body("정산 상태가 pending일 때만 송금이 가능합니다.");
        }

        // 실제 송금 수행
        boolean ok = settlementService.transferToUser(settlementsId);
        return ok
                ? ResponseEntity.ok("송금 완료 및 상태 processing으로 변경됨")
                : ResponseEntity.badRequest().body("송금 실패: 잔액 부족 또는 처리 오류");
    }

    // 내부애서 사용
    private boolean isValidStatus(String status) {
        if(status == null) return false;
        switch (status.toUpperCase()) {
            case "PENDING":
            case "PROCESSING":
            case "COMPLETED":
                return true;
            default:
                return false;
        }
    }

    // 로그인 사용자 ID 추출 -> 추후, security와 연동 후 정식으로 교체
    private int extractUserId(Principal principal) {
        //if(principal == null) {
        //    log.warn("Principal is null");
        //    return 1;
        //}
        //try {
        //    return Integer.parseInt(principal.getName());
        //} catch (Exception e) {
        //    log.warn("Principal parse 실 [{}]", principal.getName());
        //    return 1;
        //}
        return 3;
    }

    // 주어진 settlementId의 sender가 로그인 사용자와 같은지 확인
    private boolean isSender(int settlementId, int loginUserId) {
        SettlementVO vo;
        try{
            vo = settlementService.getById(settlementId);
        } catch (NoSuchElementException e) {
            log.warn("settlementId={} 찾을 수 없음", settlementId);
            return false;
        }
        Integer sender = vo.getSenderId();
        return sender != null && sender == loginUserId;
    }
}
