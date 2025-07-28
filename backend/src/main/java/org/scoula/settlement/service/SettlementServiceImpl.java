package org.scoula.settlement.service;

import lombok.RequiredArgsConstructor;
import org.scoula.group.mapper.GroupMapper;
import lombok.extern.log4j.Log4j2;
import org.scoula.group.service.GroupService;
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
import java.util.stream.Collectors;

@Log4j2
@Service
@RequiredArgsConstructor
public class SettlementServiceImpl implements SettlementService {
    private final SettlementMapper mapper;
    private final SettlementAccountMapper settlementAccountMapper;
    // private final GroupService groupService;
    private final GroupMapper groupMapper;
    // private final MemberMapper memberMapper;
    private final SettlementCalculator settlementCalculator; // n빵 계산 서비스

    // ==================== 조회 관련 ====================

    @Override
    public List<SettlementVO> getSettlementsByUserId(int userId) {
        log.info("🟢 getSettlementsByUserId 의 userId: " + userId);
        return mapper.getSettlementsByUserId(userId);
    }

    @Override
    public List<SettlementVO> getSettlementsByTripId(int tripId) {
        log.info("🟢 getSettlementsByTripId 의 tripId: " + tripId);
        return mapper.getSettlementsByTripId(tripId);
    }

    @Override
    public List<SettlementVO> getSettlementsByStatus(String status) {
        log.info("🟢 getSettlementsByStatus 의 status: " + status);
        return mapper.getSettlementsByStatus(status);
    }

    @Override
    public SettlementVO getById(int settlementId) {
        log.info("🟢 getById 의 settlementId: " + settlementId);
        return Optional.ofNullable(mapper.getById(settlementId))
                .orElseThrow(() -> new NoSuchElementException("해당 정산 내역이 존재하지 않습니다."));
    }

    @Override
    public SettlementDTO.PersonalSettlementResponseDto getMySettlements(int userId, int tripId) {
        log.info("🟢 getMySettlements - userId: {}, tripId: {}", userId, tripId);

        List<SettlementVO> allMySettlements = mapper.getSettlementsByUserId(userId)
                .stream()
                .filter(vo -> vo.getTripId().equals(tripId))
                .collect(Collectors.toList());

        // 내가 보내야 할 정산들
        List<SettlementDTO.OptimizedTransaction> toSend = allMySettlements.stream()
                .filter(vo -> vo.getSenderId().equals(userId))
                .map(this::toOptimizedTransaction)
                .collect(Collectors.toList());

        // 내가 받아야 할 정산들
        List<SettlementDTO.OptimizedTransaction> toReceive = allMySettlements.stream()
                .filter(vo -> vo.getReceiverId().equals(userId))
                .map(this::toOptimizedTransaction)
                .collect(Collectors.toList());

        // 전체 상태 계산
        String overallStatus = calculateOverallStatus(allMySettlements, userId);

        SettlementDTO.PersonalSettlementResponseDto result = new SettlementDTO.PersonalSettlementResponseDto();
        result.setToSend(toSend);
        result.setToReceive(toReceive);
        result.setOverallStatus(overallStatus);

        return result;
    }

    @Override
    public List<SettlementVO> getMyOutgoingSettlements(int userId, int tripId) {
        return mapper.getSettlementsByUserId(userId).stream()
                .filter(vo -> vo.getSenderId().equals(userId) && vo.getTripId().equals(tripId))
                .collect(Collectors.toList());
    }

    @Override
    public List<SettlementVO> getMyIncomingSettlements(int userId, int tripId) {
        return mapper.getSettlementsByUserId(userId).stream()
                .filter(vo -> vo.getReceiverId().equals(userId) && vo.getTripId().equals(tripId))
                .collect(Collectors.toList());
    }

    // ==================== 정산 요청 생성 ====================

    @Transactional
    @Override
    public SettlementDTO.CreateSettlementResponseDto createSettlementRequest(int userId, int tripId) {
        log.info("🟢 createSettlementRequest - userId: {}, tripId: {}", userId, tripId);

        SettlementDTO.CreateSettlementResponseDto response = new SettlementDTO.CreateSettlementResponseDto();

        // 그룹장 권한 체크
        if (!canRequestSettlement(userId, tripId)) {
            response.setSuccess(false);
            response.setMessage("정산 요청 권한이 없습니다.");
            return response;
        }

        // 이미 정산이 생성된 경우 체크
        if (!getPendingOrProcessingByTripId(tripId).isEmpty()) {
            response.setSuccess(false);
            response.setMessage("이미 정산이 요청된 여행입니다.");
            return response;
        }

        try {
            // n빵 계산 서비스 호출
            // List<SettlementDTO.OptimizedTransaction> results = settlementCalculator.calculate(tripId);

            // 테스트용
            List<SettlementDTO.OptimizedTransaction> results = getDummyCalculationResults();

            // 계산 결과를 DB에 저장
            saveCalculatedResults(results, tripId);

            response.setSuccess(true);
            response.setMessage("정산 요청이 성공적으로 생성되었습니다.");
            response.setCreatedCount(results.size());
            response.setSettlements(results);

            return response;
        } catch (Exception e) {
            log.error("정산 요청 생성 실패", e);
            response.setSuccess(false);
            response.setMessage("정산 요청 생성 중 오류가 발생했습니다.");
            return response;
        }
    }

    @Transactional
    @Override
    public void saveCalculatedResults(List<SettlementDTO.OptimizedTransaction> results, int tripId) {
        for (SettlementDTO.OptimizedTransaction dto : results) {
            int senderId = resolveUserId(dto.getSenderNickname());
            int receiverId = resolveUserId(dto.getReceiverNickname());
            SettlementVO vo = toVO(dto, senderId, receiverId, tripId);
            mapper.insertSettlement(vo);
        }
    }

    @Override
    public boolean canRequestSettlement(int userId, int tripId) {
        // 실제 그룹장 권한 체크
        // return groupService.isOwner(tripId, userId);

        // 테스트 : 항상 true 반환
        log.info("그룹장 권한 체크 건너뜀 (머지 후 활성화) - userId: {}, tripId: {}", userId, tripId);
        return true;
    }

    // ==================== 상태 업데이트 관련 ====================

    @Override
    public int updateSettlementStatus(int settlementId, String newStatus) {
        log.info("🟢 updateSettlementStatus 의 settlementId: {} 그리고 newStatus: {}", settlementId, newStatus);
        return mapper.updateSettlementStatus(settlementId, newStatus);
    }

    @Transactional
    @Override
    public SettlementDTO.CompleteSettlementResponseDto markAsCompleted(int settlementId, int userId) {
        log.info("🟢 markAsCompleted - settlementId: {}, userId: {}", settlementId, userId);

        SettlementDTO.CompleteSettlementResponseDto response = new SettlementDTO.CompleteSettlementResponseDto();

        try {
            SettlementVO vo = mapper.getById(settlementId);
            if (vo == null) {
                response.setSuccess(false);
                response.setMessage("정산 내역을 찾을 수 없습니다.");
                return response;
            }

            // 권한 체크
            if (!vo.getSenderId().equals(userId)) {
                response.setSuccess(false);
                response.setMessage("정산 완료 권한이 없습니다.");
                return response;
            }

            // 상태 체크
            if (!"PROCESSING".equalsIgnoreCase(vo.getSettlementStatus())) {
                response.setSuccess(false);
                response.setMessage("PROCESSING 상태에서만 완료 처리 가능합니다.");
                return response;
            }

            int updated = mapper.updateSettlementStatus(settlementId, "COMPLETED");
            if (updated == 1) {
                response.setSuccess(true);
                response.setMessage("정산이 완료되었습니다.");
                response.setNewStatus("COMPLETED");
            } else {
                response.setSuccess(false);
                response.setMessage("정산 완료 처리 실패");
            }

            return response;
        } catch (Exception e) {
            log.error("정산 완료 처리 실패", e);
            response.setSuccess(false);
            response.setMessage("정산 완료 처리 중 오류가 발생했습니다.");
            return response;
        }
    }

    @Override
    public boolean canStartTransfer(int settlementId, int userId) {
        try {
            SettlementVO vo = mapper.getById(settlementId);
            return vo != null &&
                    vo.getSenderId().equals(userId) &&
                    "PENDING".equalsIgnoreCase(vo.getSettlementStatus());
        } catch (Exception e) {
            log.error("송금 시작 권한 체크 실패", e);
            return false;
        }
    }

    // ==================== 송금 처리 ====================

    @Transactional
    @Override
    public SettlementDTO.TransferResponseDto transferToUser(int settlementId, int userId) {
        log.info("🟢 송금 요청 - settlementId: {}, userId: {}", settlementId, userId);

        SettlementDTO.TransferResponseDto response = new SettlementDTO.TransferResponseDto();

        try {
            // 정산 내역 조회
            SettlementVO vo = mapper.getById(settlementId);
            if (vo == null) {
                response.setSuccess(false);
                response.setMessage("정산 정보를 찾을 수 없습니다.");
                return response;
            }

            // 권한 체크
            if (!vo.getSenderId().equals(userId)) {
                response.setSuccess(false);
                response.setMessage("송금 권한이 없습니다.");
                return response;
            }

            // 상태 체크
            if (!"PENDING".equalsIgnoreCase(vo.getSettlementStatus())) {
                response.setSuccess(false);
                response.setMessage("PENDING 상태에서만 송금이 가능합니다.");
                return response;
            }

            int senderId = vo.getSenderId();
            int receiverId = vo.getReceiverId();
            int amount = vo.getAmount();

            // 송금자 계좌 잔액 차감
            int debitResult = settlementAccountMapper.debitIfEnough(senderId, amount);
            if (debitResult == 0) {
                response.setSuccess(false);
                response.setMessage("잔액이 부족합니다.");
                return response;
            }

            // 수신자 계좌에 입금
            int creditResult = settlementAccountMapper.credit(receiverId, amount);
            if (creditResult == 0) {
                throw new IllegalStateException("입금 실패. 롤백합니다.");
            }

            // 정산 상태 업데이트
            int updated = mapper.updateSettlementStatus(settlementId, "PROCESSING");
            if (updated == 0) {
                throw new IllegalStateException("정산 상태 업데이트 실패. 롤백합니다.");
            }

            // 송금 후 잔액 조회
            Integer senderBalance = settlementAccountMapper.selectBalance(senderId);

            response.setSuccess(true);
            response.setMessage("송금이 완료되었습니다.");
            response.setNewStatus("PROCESSING");
            response.setSenderBalance(senderBalance != null ? senderBalance : 0);

            log.info("🟢 송금 성공 - {} -> {}, 금액: {}", senderId, receiverId, amount);
            return response;

        } catch (Exception e) {
            log.error("송금 처리 실패", e);
            response.setSuccess(false);
            response.setMessage("송금 처리 중 오류가 발생했습니다.");
            return response;
        }
    }

    // ==================== 상태 확인 ====================

    @Override
    public List<SettlementVO> getPendingOrProcessingByTripId(int tripId) {
        log.info("🟢 getPendingOrProcessingByTripId 의 tripId: " + tripId);
        return mapper.getPendingOrProcessingByTripId(tripId);
    }

    @Override
    public SettlementDTO.MySettlementStatusResponseDto getMyOverallSettlementStatus(int userId) {
        List<SettlementVO> list = mapper.getSettlementsByUserId(userId);

        SettlementDTO.MySettlementStatusResponseDto response = new SettlementDTO.MySettlementStatusResponseDto();

        // 보내야 할 정산들 상태별 카운트

        // 보낼 돈 중 pending 상태 건수
        int pendingToSend = (int) list.stream()
                .filter(vo -> userId == vo.getSenderId()) // 내가 송금자
                .filter(vo -> "PENDING".equalsIgnoreCase(vo.getSettlementStatus())) // 상태 pending
                .count();

        // 보낼 돈 중 processing 상태 건수
        int processingToSend = (int) list.stream()
                .filter(vo -> userId == vo.getSenderId())
                .filter(vo -> "PROCESSING".equalsIgnoreCase(vo.getSettlementStatus()))
                .count();

        // 받아야 할 정산들 상태별 카운트

        // 받을 돈 중 pending 상태 건수
        int pendingToReceive = (int) list.stream()
                .filter(vo -> userId == vo.getReceiverId()) // 내가 수신자
                .filter(vo -> "PENDING".equalsIgnoreCase(vo.getSettlementStatus()))
                .count();

        // 받을 돈 중 processing 상태 건수
        int processingToReceive = (int) list.stream()
                .filter(vo -> userId == vo.getReceiverId())
                .filter(vo -> "PROCESSING".equalsIgnoreCase(vo.getSettlementStatus()))
                .count();

        // 완료된 건수 : 송금자/수신자 여부와 상관없이 상태가 completed인 모든 건수 계산
        int completed = (int) list.stream()
                .filter(vo -> "COMPLETED".equalsIgnoreCase(vo.getSettlementStatus()))
                .count();

        // 전체 상태 결정
        // 보낼 돈, 받을 돈 중 pending 또는 processing 상태의 건이 0개라면 -> 전체 정산 상태를 completed로 판단
        // 하나라도 pending 또는 processing이 있으면 -> 전체 상태는 processing으로 판단
        String overallStatus;
        if (pendingToSend + processingToSend + pendingToReceive + processingToReceive == 0) {
            overallStatus = "COMPLETED";
        } else {
            overallStatus = "PROCESSING";
        }

        // DTO에 값 세팅 -> 사용자의 현재 정산 진행 상황을 상세하게 반환
        response.setOverallStatus(overallStatus);
        response.setPendingToSendCount(pendingToSend); // 내가 아직 안 보낸 돈
        response.setProcessingToSendCount(processingToSend);
        response.setPendingToReceiveCount(pendingToReceive); // 내가 아직 못 받은 돈
        response.setProcessingToReceiveCount(processingToReceive);
        response.setCompletedCount(completed);

        return response;
    }

    @Override
    public SettlementDTO.RemainingSettlementResponseDto getRemainingSettlements(int tripId) {
        List<SettlementVO> allSettlements = mapper.getSettlementsByTripId(tripId);

        SettlementDTO.RemainingSettlementResponseDto response = new SettlementDTO.RemainingSettlementResponseDto();

        int pendingCount = (int) allSettlements.stream()
                .filter(vo -> "PENDING".equalsIgnoreCase(vo.getSettlementStatus()))
                .count();

        int processingCount = (int) allSettlements.stream()
                .filter(vo -> "PROCESSING".equalsIgnoreCase(vo.getSettlementStatus()))
                .count();

        int completedCount = (int) allSettlements.stream()
                .filter(vo -> "COMPLETED".equalsIgnoreCase(vo.getSettlementStatus()))
                .count();

        response.setHasRemaining(pendingCount + processingCount > 0);
        response.setTotalCount(allSettlements.size());
        response.setPendingCount(pendingCount);
        response.setProcessingCount(processingCount);
        response.setCompletedCount(completedCount);

        return response;
    }

    // ==================== 내부 메서드들 ====================

    private SettlementVO toVO(SettlementDTO.OptimizedTransaction dto, int senderId, int receiverId, int tripId) {
        return SettlementVO.builder()
                .senderId(senderId)
                .receiverId(receiverId)
                .amount(dto.getAmount())
                .tripId(tripId)
                .settlementStatus("PENDING")
                .build();
    }

    private SettlementDTO.OptimizedTransaction toOptimizedTransaction(SettlementVO vo) {
        SettlementDTO.OptimizedTransaction dto = new SettlementDTO.OptimizedTransaction();
        dto.setSettlementId(vo.getSettlementId());
        dto.setSenderNickname(resolveNickname(vo.getSenderId()));
        dto.setReceiverNickname(resolveNickname(vo.getReceiverId()));
        dto.setAmount(vo.getAmount());
        dto.setStatus(vo.getSettlementStatus());
        return dto;
    }

    private String calculateOverallStatus(List<SettlementVO> settlements, int userId) {
        boolean hasPendingOrProcessingToSend = settlements.stream()
                .filter(vo -> userId == vo.getSenderId())
                .anyMatch(vo -> !"COMPLETED".equalsIgnoreCase(vo.getSettlementStatus()));

        boolean hasPendingOrProcessingToReceive = settlements.stream()
                .filter(vo -> userId == vo.getReceiverId())
                .anyMatch(vo -> !"COMPLETED".equalsIgnoreCase(vo.getSettlementStatus()));

        if (!hasPendingOrProcessingToSend && !hasPendingOrProcessingToReceive) {
            return "COMPLETED";
        } else {
            return "PROCESSING";
        }
    }

    private int resolveUserId(String nickname) {
        // MemberMapper 연동
        // Integer userId = memberMapper.findUserIdByNickname(nickname);
        // if(userId == null) {
        //     throw new IllegalArgumentException("존재하지 않는 닉네임: " + nickname);
        // }
        // return userId;

        // 테스트용
        switch (nickname) {
            case "김민수": return 1;
            case "이건우": return 2;
            case "최정훈": return 3;
            case "권준호": return 4;
            case "앨리스": return 5;
            case "밥": return 6;
            case "찰리": return 7;
            case "다이애나": return 8;
            // 기존 테스트 데이터도 호환
            case "A": return 1;
            case "B": return 2;
            case "C": return 3;
            default: throw new IllegalArgumentException("지원되지 않는 닉네임 (머지 후 해결 예정): " + nickname);
        }
    }

    private String resolveNickname(int userId) {
        // MemberMapper 연동
        // return memberMapper.findNicknameByUserId(userId);

        // 테스트용
        switch (userId) {
            case 1: return "김민수";
            case 2: return "이건우";
            case 3: return "최정훈";
            case 4: return "권준호";
            case 5: return "앨리스";
            case 6: return "밥";
            case 7: return "찰리";
            case 8: return "다이애나";
            default: return "Unknown-" + userId;
        }
    }

    private List<SettlementDTO.OptimizedTransaction> getDummyCalculationResults() {
        // 테스트용 더미 데이터
        SettlementDTO.OptimizedTransaction tx1 = new SettlementDTO.OptimizedTransaction();
        tx1.setSenderNickname("김민수");
        tx1.setReceiverNickname("이건우");
        tx1.setAmount(10000);

        SettlementDTO.OptimizedTransaction tx2 = new SettlementDTO.OptimizedTransaction();
        tx2.setSenderNickname("최정훈");
        tx2.setReceiverNickname("이건우");
        tx2.setAmount(5000);

        return List.of(tx1, tx2);
    }
  
    /* 정산하기 1 페이지에 필요한 요약 정보를 추출 */    @Override
    public SettlementDTO.SettlementSummaryResponseDto getSettlementSummary(int tripId) {

        // 1. Mapper를 통해 DB에서 총 사용 금액 조회
        // 만약 결제 내역이 없어 null이 반환될 경우, 0으로 처리
        Integer totalAmount = mapper.getTotalAmountByTripId(tripId);
        if (totalAmount == null) {
            totalAmount = 0;
        }

        // 2. Mapper를 통해 DB에서 멤버별 총 결제 금액 목록 조회
        List<SettlementDTO.MemberPaymentInfo> memberPayments = mapper.getMemberPaymentsByTripId(tripId);

        // 3. 조회된 데이터들을 DTO에 담아서 반환
        SettlementDTO.SettlementSummaryResponseDto summaryDto = new SettlementDTO.SettlementSummaryResponseDto();

        // To-do: tripMapper를 사용하여 tripId로 여행 이름을 조회하고 설정해야 함.
        // summaryDto.setTripName(tripMapper.getTripNameById(tripId));
        summaryDto.setTripName("서울 우정여행"); // 현재는 임시 데이터 사용
        summaryDto.setTotalAmount(totalAmount);
        summaryDto.setMemberPayments(memberPayments);

        return summaryDto;
    }

    /**
     * [NEW] 정산 2단계: 최종 정산 결과를 계산하여 반환함.
     *
     * @param tripId 조회할 여행의 ID
     * @return 상계 처리가 완료된 최종 송금 목록을 담은 DTO
     */
    @Override
    public SettlementDTO.SettlementResultResponseDto calculateFinalSettlement(int tripId) {
        // 1. DB에서 정산 계산에 필요한 원본 데이터 조회
        List<SettlementDTO.RawSettlementDataDTO> rawData = mapper.getRawSettlementDataByTripId(tripId);

        // 2. 해당 여행의 전체 멤버 닉네임 목록 조회
        List<String> members = groupMapper.findNicknamesByTripId(tripId);

        // 3. 계산기에 데이터를 넘겨 최종 송금 목록 계산 요청
        List<SettlementDTO.OptimizedTransaction> transactions = settlementCalculator.calculate(rawData, members);

        // 4. DTO에 담아 Controller로 반환
        SettlementDTO.SettlementResultResponseDto resultDto = new SettlementDTO.SettlementResultResponseDto();

        Integer totalAmount = mapper.getTotalAmountByTripId(tripId);
        resultDto.setTotalAmount(totalAmount != null ? totalAmount : 0);
        resultDto.setMembers(members);
        resultDto.setTransactions(transactions);

        // To-do: tripMapper를 사용하여 실제 여행 이름을 조회해야 합니다.
        // String tripName = tripMapper.findTripNameById(tripId);
        // resultDto.setTripName(tripName);
        resultDto.setTripName("서울 우정여행"); // 현재는 임시 데이터 사용

        return resultDto;
    }
}
