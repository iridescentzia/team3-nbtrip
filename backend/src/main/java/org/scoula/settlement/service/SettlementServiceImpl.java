package org.scoula.settlement.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.scoula.member.mapper.MemberMapper;
import org.scoula.settlement.domain.SettlementVO;
import org.scoula.settlement.dto.SettlementDTO;
import org.scoula.settlement.mapper.SettlementAccountMapper;
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
    private final SettlementAccountMapper settlementAccountMapper;
    //private final MemberMapper memberMapper;

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

    // 그룹원 간 송금 처리
    @Transactional
    @Override
    public boolean transferToUser(int settlementId) {
        log.info("🟢 송금 요청 - settlementId: " + settlementId);

        // 정산 내역 조회
        SettlementVO vo = mapper.getById(settlementId);
        if(vo == null) {
            log.warn("🟢 송금 실패 - 정산 정보 없음");
            return false;
        }

        if(!"PENDING".equalsIgnoreCase(vo.getSettlementStatus())) {
            log.warn("🟢 송금 실패 - 상태가 pending이 아님: {}", vo.getSettlementStatus());
            return false;
        }

        int senderId = vo.getSenderId();
        int receiverId = vo.getReceiverId();
        int amount = vo.getAmount();

        // 송금자 계좌 잔액 차감
        int result1 = settlementAccountMapper.debitIfEnough(senderId, amount);
        if(result1 == 0) {
            log.warn("🟢 송금 실패 - 잔액 부족 또는 동시성 문제 발생. senderId: " + senderId + " amount: " + amount);
            return false;
        }

        // 수신자 계좌에 입금
        int result2= settlementAccountMapper.credit(receiverId, amount);
        if(result2 == 0) {
            log.warn("🟢 입금 실패 - receiverId: " + receiverId);
            throw new IllegalStateException("입금 실패. 롤백합니다.");
        }

        // 정산 상태 업데이트
        int updated = mapper.updateSettlementStatus(settlementId, "PROCESSING");
        if(updated == 0) {
            log.warn("🟢 상태 업데이트 실패 - settlementId: " + settlementId);
            throw new IllegalStateException("정산 상태 업데이트 실패. 롤백합니다.");
        }

        log.info("🟢 송금 성공 - {} -> {}, 금액: {}", senderId, receiverId, amount);
        return true;
    }

    // n빵 분배 결과 저장
    @Transactional
    public void saveCalculatedResults(List<SettlementDTO.OptimizedTransaction> results, int tripId) {
        for(SettlementDTO.OptimizedTransaction dto : results) {
            int senderId = resolveUserId(dto.getSenderNickname());
            int receiverId = resolveUserId(dto.getReceiverNickname());
            SettlementVO vo = toVO(dto, senderId, receiverId, tripId);
            mapper.insertSettlement(vo);
        }
    }

    // 사용자의 정산 상태 조회
    @Override
    public String getMyOverallSettlementStatus(int userId) {
        List<SettlementVO> list = mapper.getSettlementsByUserId(userId);

        boolean hasPendingOrProcessingToSend = list.stream()
                .filter(vo -> userId == vo.getSenderId())
                .anyMatch(vo -> !"COMPLETED".equalsIgnoreCase(vo.getSettlementStatus()));

        boolean hasPendingOrProcessingToReceive = list.stream()
                .filter(vo -> userId == vo.getReceiverId())
                .anyMatch(vo -> !"COMPLETED".equalsIgnoreCase(vo.getSettlementStatus()));

        if(!hasPendingOrProcessingToSend && !hasPendingOrProcessingToReceive) {
            return "COMPLETED";
        } else {
            return "PROCESSING";
        }
    }

    private SettlementVO toVO(SettlementDTO.OptimizedTransaction dto, int senderId, int receiverId, int tripId){
        return new SettlementVO().builder()
                .senderId(senderId)
                .receiverId(receiverId)
                .amount(dto.getAmount())
                .tripId(tripId)
                .settlementStatus("PENDING")
                .build();
    }

    private int resolveUserId(String nickname) {
        //Integer userId = memberMapper.findUserIdByNickname(nickname);
        //if(userId == null) {
            //throw new IllegalArgumentException("존재하지 않는 닉네임: " + nickname);
        //}
        //return userId;

        // 테스트용
        switch (nickname) {
            case "A" : return 1;
            case "B" : return 2;
            case "C" : return 3;
            default: throw new IllegalArgumentException("자원되지 않는 닉네임: " + nickname);
        }
    }
}
