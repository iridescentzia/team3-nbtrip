package org.scoula.settlement.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.scoula.settlement.dto.SettlementDTO;
import java.util.List;

@Mapper
public interface SettlementMapper {

    /* 1. 특정 여행의 총 사용 금액을 조회 */
    Integer getTotalAmountByTripId(Long tripId);

    /* 2. 특정 여행의 멤버별 총 결제 금액 목록을 조회 */
    List<SettlementDTO.MemberPaymentInfo> getMemberPaymentsByTripId(Long tripId);
}
