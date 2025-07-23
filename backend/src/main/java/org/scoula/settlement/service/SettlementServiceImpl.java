package org.scoula.settlement.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.scoula.settlement.domain.SettlementVO;
import org.scoula.settlement.mapper.SettlementMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Log4j2
@Service
@RequiredArgsConstructor
public class SettlementServiceImpl implements SettlementService {
    private final SettlementMapper mapper;

    // 특정 유저의 정산 내역 조회
    @Override
    public List<SettlementVO> getSettlementsByUserId(int userId) {
        log.info("🟢 getSettlementsByUserId 의 userId: " + userId);
        return mapper.getSettlementsByUserId(userId);
    }

    // 특정 여행의 정산 내역 조회
    @Override
    public List<SettlementVO> getSettlementsByTripId(int tripId) {
        log.info("🟢 getSettlementsByTripId 의 tripId: " + tripId);
        return mapper.getSettlementsByTripId(tripId);
    }

    // 상태별 정산 내역 조회
    @Override
    public List<SettlementVO> getSettlementsByStatus(String status) {
        log.info("🟢 getSettlementsByStatus 의 status: " + status);
        return mapper.getSettlementsByStatus(status);
    }

    // 단건 조회
    @Override
    public SettlementVO getById(int settlementId) {
        log.info("🟢 getById 의 settlementId: " + settlementId);

        return Optional.ofNullable(mapper.getById(settlementId))
                .orElseThrow(() -> new NoSuchElementException("해당 정산 내역이 존재하지 않습니다."));
    }

    // 상태 업데이트 (내부 관리용)
    @Override
    public int updateSettlementStatus(int settlementId, String newStatus) {
        log.info("🟢 updateSettlementStatus 의 settlementId: " + settlementId + " 그리고 newStatus: " + newStatus);
        return mapper.updateSettlementStatus(settlementId, newStatus);
    }

    // 송금 완료 처리용 (실제 사용자가 송금 버튼 누를 때 사용)
    @Transactional
    @Override
    public boolean markAsCompleted(int settlementId) {
        log.info("🟢 markAsCompleted 의 settlementId: " + settlementId);

        // 정산 내역 존재 여부 확인
        SettlementVO vo = mapper.getById(settlementId); // 예외 던지지 않고 직접 호출
        if(vo == null) {
            log.warn("정산 완료 실패 - 내역 없음 id={}", settlementId);
            return false;
        }

        if(!"PROCESSING".equalsIgnoreCase(vo.getSettlementStatus())) {
            log.warn("정산 완료 실패 - 상태가 PROCESSING 아님 id={}, status={}", settlementId, vo.getSettlementStatus());
            return false;
        }

        return mapper.updateSettlementStatus(settlementId, "COMPLETED") == 1;
    }

    // 여행 내 미완료된 정산 목록 조회
    @Override
    public List<SettlementVO> getPendingOrProcessingByTripId(int tripId) {
        log.info("🟢 getPendingOrProcessingByTripId 의 tripId: " + tripId);
        return mapper.getPendingOrProcessingByTripId(tripId);
    }
}
