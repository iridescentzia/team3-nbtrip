package org.scoula.settlement.service;

import lombok.RequiredArgsConstructor;
import org.scoula.group.service.GroupService;
import org.scoula.settlement.dto.SettlementDTO;
import org.scoula.settlement.mapper.SettlementMapper;
import org.scoula.group.mapper.GroupMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SettlementServiceImpl implements SettlementService {

    // 의존성 주입(DI)
    private final SettlementMapper settlementMapper;
    private final GroupMapper groupMapper;
    private final SettlementCalculator settlementCalculator;
    // private final TripMapper tripMapper; // 여행 이름을 가져오기 위해 필요 (추후 추가)

    /* 정산하기 1 페이지에 필요한 요약 정보를 추출 */    @Override
    public SettlementDTO.SettlementSummaryResponseDto getSettlementSummary(Long tripId) {

        // 1. Mapper를 통해 DB에서 총 사용 금액 조회
        // 만약 결제 내역이 없어 null이 반환될 경우, 0으로 처리
        Integer totalAmount = settlementMapper.getTotalAmountByTripId(tripId);
        if (totalAmount == null) {
            totalAmount = 0;
        }

        // 2. Mapper를 통해 DB에서 멤버별 총 결제 금액 목록 조회
        List<SettlementDTO.MemberPaymentInfo> memberPayments = settlementMapper.getMemberPaymentsByTripId(tripId);

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
    public SettlementDTO.SettlementResultResponseDto calculateFinalSettlement(Long tripId) {
        // 1. DB에서 정산 계산에 필요한 원본 데이터 조회
        List<SettlementDTO.RawSettlementDataDTO> rawData = settlementMapper.getRawSettlementDataByTripId(tripId);

        // 2. 해당 여행의 전체 멤버 닉네임 목록 조회
        List<String> members = groupMapper.findNicknamesByTripId(tripId);

        // 3. 계산기에 데이터를 넘겨 최종 송금 목록 계산 요청
        List<SettlementDTO.OptimizedTransaction> transactions = settlementCalculator.calculate(rawData, members);

        // 4. DTO에 담아 Controller로 반환
        SettlementDTO.SettlementResultResponseDto resultDto = new SettlementDTO.SettlementResultResponseDto();

        Integer totalAmount = settlementMapper.getTotalAmountByTripId(tripId);
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