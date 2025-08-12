package org.scoula.settlement.service;

import lombok.RequiredArgsConstructor;
import org.scoula.trip.mapper.TripMapper;
import lombok.extern.log4j.Log4j2;
import org.scoula.trip.service.TripService;
import org.scoula.member.mapper.MemberMapper;
import org.scoula.settlement.domain.SettlementVO;
import org.scoula.settlement.dto.SettlementDTO;
import org.scoula.settlement.mapper.SettlementAccountMapper;
import org.scoula.settlement.mapper.SettlementMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Log4j2
@Service
@RequiredArgsConstructor
public class SettlementServiceImpl implements SettlementService {
    private final SettlementMapper mapper;
    private final SettlementAccountMapper settlementAccountMapper;
    private final TripService tripService;
    private final TripMapper tripMapper;
    private final MemberMapper memberMapper;
    private final SettlementCalculator settlementCalculator; // n빵 계산 서비스
    private final IndividualTransferProcessor individualTransferProcessor;

    // ==================== 조회 관련 ====================

    @Override
    public List<SettlementVO> getSettlementsByUserId(int userId) {
        log.info("🟢 getSettlementsByUserId 의 userId: " + userId);
        return mapper.getSettlementsWithNicknamesByUserId(userId);
    }

    @Override
    public List<SettlementVO> getSettlementsByTripId(int tripId) {
        log.info("🟢 getSettlementsByTripId 의 tripId: " + tripId);
        return mapper.getSettlementsWithNicknamesByTripId(tripId);
    }

    @Override
    public List<SettlementVO> getSettlementsByStatus(String status) {
        log.info("🟢 getSettlementsByStatus 의 status: " + status);
        return mapper.getSettlementsByStatus(status);
    }

    @Override
    public SettlementVO getById(int settlementId) {
        log.info("🟢 getById 의 settlementId: " + settlementId);
        return Optional.ofNullable(mapper.getByIdWithNicknames(settlementId))
                .orElseThrow(() -> new NoSuchElementException("해당 정산 내역이 존재하지 않습니다."));
    }

    @Override
    public SettlementDTO.PersonalSettlementResponseDto getMySettlements(int userId, int tripId) {
        log.info("🟢 getMySettlements - userId: {}, tripId: {}", userId, tripId);

        // DB에서 바로 특정 여행의 정산만 조회
        List<SettlementVO> allMySettlements = mapper.getMySettlementsByTripId(userId, tripId);

        // 내가 보내야 할 정산들
        List<SettlementDTO.OptimizedTransactionWithNickname> toSend = allMySettlements.stream()
                .filter(vo -> vo.getSenderId().equals(userId))
                .map(this::toOptimizedTransactionWithNickname)
                .collect(Collectors.toList());

        // 내가 받아야 할 정산들
        List<SettlementDTO.OptimizedTransactionWithNickname> toReceive = allMySettlements.stream()
                .filter(vo -> vo.getReceiverId().equals(userId))
                .map(this::toOptimizedTransactionWithNickname)
                .collect(Collectors.toList());

        // 전체 상태 계산
        String overallStatus = calculateOverallStatus(allMySettlements, userId);

        SettlementDTO.PersonalSettlementResponseDto result = new SettlementDTO.PersonalSettlementResponseDto();
        result.setToSend(toSend);
        result.setToReceive(toReceive);
        result.setOverallStatus(overallStatus);

        try {
            Integer totalAmount = mapper.getTotalAmountByTripId(tripId);
            result.setTotalAmount(totalAmount != null ? totalAmount : 0);

            String tripName = tripMapper.findTripNameById(tripId);
            result.setTripName(tripName != null ? tripName : "여행");

            log.info("🔍 조회된 여행 정보 - tripName: {}, totalAmount: {}", tripName, totalAmount);

        } catch (Exception e) {
            log.warn("여행 정보 조회 실패 - tripId: {}", tripId, e);
            result.setTotalAmount(0);
            result.setTripName("여행");
        }

        return result;
    }

    @Override
    public List<SettlementVO> getMyOutgoingSettlements(int userId, int tripId) {
        return mapper.getMyOutgoingSettlementsByTripId(userId, tripId);
    }

    @Override
    public List<SettlementVO> getMyIncomingSettlements(int userId, int tripId) {
        return mapper.getMyIncomingSettlementsByTripId(userId, tripId);
    }

    // ==================== 정산 요청 생성 ====================

    @Transactional
    @Override
    public SettlementDTO.CreateSettlementResponseDto createSettlementRequest(int userId, int tripId) {
        log.info("🟢 createSettlementRequest - userId: {}, tripId: {}", userId, tripId);

        SettlementDTO.CreateSettlementResponseDto response = new SettlementDTO.CreateSettlementResponseDto();

        // n빵 계산 가능 여부 확인
        if (!canCalculateSettlement(tripId)) {
            response.setSuccess(false);
            response.setMessage("정산 계산이 불가능한 상태입니다.");
            return response;
        }

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
            List<SettlementDTO.RawSettlementDataDTO> rawData = mapper.getRawSettlementDataByTripId(tripId);
            List<Integer> memberIds = tripMapper.findUserIdsByTripId(tripId);

            // 데이터 유효성 검증
            if(rawData.isEmpty()) {
                response.setSuccess(false);
                response.setMessage("정산할 결제 내역이 없습니다.");
                return response;
            }

            if(memberIds.isEmpty()) {
                response.setSuccess(false);
                response.setMessage("여행 멤버 정보를 찾을 수 없습니다.");
                return response;
            }

            // n빵 계산 실행
            List<SettlementDTO.OptimizedTransaction> results = settlementCalculator.calculate(rawData, memberIds);

            if(results.isEmpty()) {
                response.setSuccess(false);
                response.setMessage("정산알 내역이 없습니다. (모든 멤버의 지출이 동일)");
                return response;
            }

            // 계산 결과를 DB에 저장
            saveCalculatedResults(results, tripId);

            response.setSuccess(true);
            response.setMessage("정산 요청이 성공적으로 생성되었습니다.");
            response.setCreatedCount(results.size());
            response.setSettlements(results);

            return response;
        } catch (Exception e) {
            log.error("정산 요청 생성 실패 - tripId: {}", tripId, e);
            response.setSuccess(false);
            response.setMessage("정산 요청 생성 중 오류가 발생했습니다: " + e.getMessage());
            return response;
        }
    }

    @Transactional
    @Override
    public void saveCalculatedResults(List<SettlementDTO.OptimizedTransaction> results, int tripId) {
        for (SettlementDTO.OptimizedTransaction dto : results) {
            int senderId = dto.getSenderId();
            int receiverId = dto.getReceiverId();
            SettlementVO vo = toVO(dto, senderId, receiverId, tripId);
            mapper.insertSettlement(vo);
        }
    }

    @Override
    public boolean canRequestSettlement(int userId, int tripId) {
        try {
            // 실제 그룹장 권한 체크
            return tripService.isOwner(tripId, userId);
        } catch (Exception e) {
            log.error("그룹장 권한 체크 실패 - userId: {}, tripId: {}", userId, tripId, e);
            return false;
        }
    }

    // ==================== 상태 업데이트 관련 ====================

    @Override
    public int updateSettlementStatus(int settlementId, String newStatus) {
        log.info("🟢 updateSettlementStatus 의 settlementId: {} 그리고 newStatus: {}", settlementId, newStatus);
        return mapper.updateSettlementStatus(settlementId, newStatus);
    }

    // ==================== 송금 처리 ====================

    @Override  // @Transactional 제거 - 건별 독립 트랜잭션 사용
    public SettlementDTO.TransferResponseDto transferToUsers(List<Integer> settlementIds, int userId) {
        log.info("🟢 다중 송금 요청 - 총 {}건, userId: {}", settlementIds.size(), userId);

        List<SettlementDTO.TransferDetail> details = new ArrayList<>();
        List<Integer> remainingIds = new ArrayList<>();
        int successCount = 0, failCount = 0;

        for (int settlementId : settlementIds) {
            // 각 건별로 완전히 독립적인 트랜잭션 처리
            SettlementDTO.TransferDetail detail = individualTransferProcessor.processTransfer(settlementId, userId);
            details.add(detail);

            if (detail.isSuccess()) {
                successCount++;
                log.info("✅ 송금 성공 - ID: {}, 수신자: {}, 금액: {}",
                        settlementId, detail.getReceiverNickname(), detail.getAmount());
            } else {
                failCount++;
                remainingIds.add(settlementId);
                log.warn("❌ 송금 실패 - ID: {}, 사유: {}", settlementId, detail.getFailureReason());
            }
        }

        // 송금 후 최종 잔액 조회
        Integer senderBalance = settlementAccountMapper.selectBalance(userId);

        // 응답 구성
        return buildTransferResponse(details, successCount, failCount, remainingIds, senderBalance);
    }

    private SettlementDTO.TransferResponseDto buildTransferResponse(
            List<SettlementDTO.TransferDetail> details,
            int successCount, int failCount,
            List<Integer> remainingIds, Integer senderBalance) {

        SettlementDTO.TransferResponseDto response = new SettlementDTO.TransferResponseDto();
        response.setSuccess(successCount > 0);
        response.setTotalCount(details.size());
        response.setSuccessCount(successCount);
        response.setFailedCount(failCount);
        response.setDetails(details);
        response.setRemainingSettlementIds(remainingIds);
        response.setSenderBalance(senderBalance != null ? senderBalance : 0);

        // 상황별 메시지 구성
        if (failCount == 0) {
            response.setMessage(String.format("모든 송금 완료 (%d건)", successCount));
        } else if (successCount == 0) {
            response.setMessage(String.format("모든 송금 실패 (%d건)", failCount));
        } else {
            response.setMessage(String.format("부분 송금 완료: %d건 성공, %d건 실패 (미정산)", successCount, failCount));
        }

        return response;
    }

    // ==================== 상태 확인 ====================

    @Override
    public List<SettlementVO> getPendingOrProcessingByTripId(int tripId) {
        log.info("🟢 getPendingOrProcessingByTripId 의 tripId: " + tripId);
        return mapper.getPendingOrProcessingByTripId(tripId);
    }

    @Override
    public SettlementDTO.MySettlementStatusResponseDto getMyOverallSettlementStatus(int userId) {
        // ✅ DB에서 한 번에 모든 통계 계산
        SettlementDTO.MySettlementStatusResponseDto response = mapper.getMySettlementStatusCounts(userId);

        // 전체 상태 결정
        int totalPending = response.getPendingToSendCount() + response.getPendingToReceiveCount() +
                response.getProcessingToSendCount() + response.getProcessingToReceiveCount();

        response.setOverallStatus(totalPending == 0 ? "COMPLETED" : "PROCESSING");

        return response;
    }

    @Override
    public SettlementDTO.RemainingSettlementResponseDto getRemainingSettlements(int tripId) {
        // ✅ DB에서 한 번에 모든 통계 계산
        return mapper.getRemainingSettlementCounts(tripId);
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
        dto.setSenderId(vo.getSenderId());    // JOIN으로 조회된 값 사용
        dto.setReceiverId(vo.getReceiverId()); // JOIN으로 조회된 값 사용
        dto.setAmount(vo.getAmount());
        dto.setStatus(vo.getSettlementStatus());
        return dto;
    }

    private SettlementDTO.OptimizedTransactionWithNickname toOptimizedTransactionWithNickname(SettlementVO vo) {
        SettlementDTO.OptimizedTransactionWithNickname dto = new SettlementDTO.OptimizedTransactionWithNickname();
        dto.setSettlementId(vo.getSettlementId());
        dto.setSenderId(vo.getSenderId());
        dto.setReceiverId(vo.getReceiverId());
        dto.setAmount(vo.getAmount());
        dto.setStatus(vo.getSettlementStatus());
        // ✅ 닉네임 설정
        dto.setSenderNickname(vo.getSenderNickname());
        dto.setReceiverNickname(vo.getReceiverNickname());
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

    private boolean canCalculateSettlement(int tripId) {
        try {
            // 1. 결제 내역 존재 여부 체크
            Integer totalAmount = mapper.getTotalAmountByTripId(tripId);
            if (totalAmount == null || totalAmount <= 0) {
                log.warn("정산 불가 - 결제 내역 없음. tripId: {}" + tripId);
                return false;
            }

            // 2. 멤버 존재 여부 체크
            List<String> members = tripMapper.findNicknamesByTripId(tripId);
            if(members.isEmpty()) {
                log.warn("정산 불가 - 멤버 없음. tripId: {}" + tripId);
                return false;
            }

            // 3. 정산 데이터 존재 여부 체크
            List<SettlementDTO.RawSettlementDataDTO> rawData = mapper.getRawSettlementDataByTripId(tripId);
            if (rawData.isEmpty()) {
                log.warn("정산 불가 - 원본 데이터 없음. tripId: {}" + tripId);
                return false;
            }
            return true;
        } catch (Exception e) {
            log.error("정산 가능 여부 체크 실패 - tripId: {}" + tripId, e);
            return false;
        }
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

        try {
            String tripName = tripMapper.findTripNameById(tripId);
            summaryDto.setTripName(tripName != null ? tripName : "여행");
            log.info("🔍 조회된 여행 이름: {}", tripName);
        } catch (Exception e) {
            log.warn("여행 이름 조회 실패 - tripId: {}", tripId, e);
            summaryDto.setTripName("여행");
        }

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

        // 1. 해당 여행의 전체 멤버 닉네임 목록 조회
        List<String> members = tripMapper.findNicknamesByTripId(tripId);

        // 2. 최종 송금 목록 계산 요청
        List<SettlementDTO.OptimizedNicknameTransaction> transactions = mapper.getNicknameSettlementsByTripId(tripId);

        // 3. DTO에 담아 Controller로 반환
        SettlementDTO.SettlementResultResponseDto resultDto = new SettlementDTO.SettlementResultResponseDto();

        Integer totalAmount = mapper.getTotalAmountByTripId(tripId);
        resultDto.setTotalAmount(totalAmount != null ? totalAmount : 0);
        resultDto.setMembers(members);
        resultDto.setTransactions(transactions);

        try {
            String tripName = tripMapper.findTripNameById(tripId);
            resultDto.setTripName(tripName != null ? tripName : "여행");
        } catch (Exception e) {
            log.warn("여행 이름 조회 실패 - tripId: {}", tripId, e);
            resultDto.setTripName("여행");
        }

        return resultDto;
    }

    @Override
    public boolean isAllSettlementCompleted(Integer tripId) {
        try {
            log.info("🟢 전체 정산 완료 여부 확인 - tripId: {}", tripId);

            // RemainingSettlementResponseDto의 hasRemaining 필드로 확인
            // hasRemaining이 false면 모든 정산이 완료된 상태
            SettlementDTO.RemainingSettlementResponseDto result = getRemainingSettlements(tripId);

            boolean isCompleted = !result.isHasRemaining();

            log.info("🔍 정산 완료 확인 결과 - tripId: {}, 전체완료: {}, 미완료건수: {}",
                    tripId, isCompleted, result.getPendingCount() + result.getProcessingCount());

            return isCompleted;
        } catch (Exception e) {
            log.error("전체 정산 완료 확인 실패 - tripId: {}", tripId, e);
            return false; // 예외 발생 시 안전하게 false 반환
        }
    }

    @Override
    public List<SettlementDTO.UnsettledTripInfo> getUnsettledTrips(int userId) {
        // Mapper의 메소드를 호출하여 DB 작업을 위임하고, 결과를 그대로 반환함.
        return mapper.findUnsettledTripsByUserId(userId);
    }

    /**
     * [NEW] 두 사용자 간의 정산 과정 상세 내역을 조회하는 로직을 구현함.
     */

    @Override
    public SettlementDTO.SettlementBreakdownResponseDto getSettlementBreakdown(int tripId, int myUserId, int otherUserId) {

        // 1. Mapper를 통해 DB에서 두 사람 간의 모든 거래 원본 데이터를 조회
        // FIX: 반환 타입을 DTO로 변경합니다.
        List<SettlementDTO.BreakdownDetailDTO> rawDetails = mapper.findBreakdownDetails(tripId, myUserId, otherUserId);
        log.info(rawDetails.toString());


        // 2. [핵심] 조회된 결과를 payment_id를 기준으로 그룹화합니다.
        Map<Integer, List<SettlementDTO.BreakdownDetailDTO>> paymentsById = rawDetails.stream()
                .collect(Collectors.groupingBy(SettlementDTO.BreakdownDetailDTO::getPaymentId));
        log.info(paymentsById.toString());

        SettlementDTO.SettlementBreakdownResponseDto breakdownDto = new SettlementDTO.SettlementBreakdownResponseDto();
        List<SettlementDTO.BreakdownItem> items = new ArrayList<>();
        int finalAmount = 0;

        // 3. 각 결제 건(그룹)별로 순회하며 하나의 영수증 항목만 생성합니다.
        for (List<SettlementDTO.BreakdownDetailDTO> paymentDetails : paymentsById.values()) {
            SettlementDTO.BreakdownDetailDTO firstDetail = paymentDetails.get(0); // 공통 정보는 첫 번째 데이터에서 가져옴
            log.info(firstDetail.toString());

            int payerId = firstDetail.getPayerId();
            int mySplitAmount = 0;
            int otherUserSplitAmount = 0;

            // 해당 결제 건에서 나와 상대방의 분담액을 각각 찾습니다.
            for (SettlementDTO.BreakdownDetailDTO detail : paymentDetails) {
                log.info(detail.toString());
                int participantId = detail.getParticipantId();
                if (participantId == myUserId) {
                    mySplitAmount = detail.getMySplitAmount();
                }
                if (participantId == otherUserId) {
                    otherUserSplitAmount = detail.getMySplitAmount();
                }
            }

            // 하나의 결제 건에 대해, 하나의 영수증 항목(BreakdownItem)만 생성합니다.
            SettlementDTO.BreakdownItem item = new SettlementDTO.BreakdownItem();
            item.setTitle(firstDetail.getTitle());
            item.setTotalAmount(firstDetail.getTotalAmount());
            item.setPayAt(firstDetail.getPayAt());
            item.setPayerNickname(firstDetail.getPayerNickname());
            if(payerId == myUserId) {
                item.setMySplitAmount(otherUserSplitAmount);
            } else{
                item.setMySplitAmount(mySplitAmount);
            }
            item.setMyPayment(payerId == myUserId);
            items.add(item);

            // 4. 최종 상계 금액 계산
            if (payerId == myUserId) {
                // 내가 결제했다면, 상대방의 부담액만큼 받아야 함 (+)
                finalAmount += otherUserSplitAmount;
            } else if (payerId == otherUserId) {
                // 상대방이 결제했다면, 나의 부담액만큼 보내야 함 (-)
                finalAmount -= mySplitAmount;
            }
        }

        // 5. 최종 DTO에 모든 정보 설정
        breakdownDto.setItems(items);
        breakdownDto.setFinalAmount(finalAmount);
        breakdownDto.setMyNickname(memberMapper.findNicknameById(myUserId));
        breakdownDto.setOtherUserNickname(memberMapper.findNicknameById(otherUserId));

        return breakdownDto;
    }
}
