package org.scoula.settlement.service;

import lombok.RequiredArgsConstructor;
import org.scoula.settlement.dto.SettlementDTO;
import org.scoula.settlement.mapper.SettlementMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SettlementServiceImpl implements SettlementService {

    // 의존성 주입(DI)
    private final SettlementMapper settlementMapper;
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
}