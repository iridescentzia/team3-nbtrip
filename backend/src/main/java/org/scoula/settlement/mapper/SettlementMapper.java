package org.scoula.settlement.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.scoula.settlement.dto.SettlementDTO;
import java.util.List;
import java.util.Map;

@Mapper
public interface SettlementMapper {


    /**
     * 1. 특정 여행의 총 사용 금액을 조회함.
     *
     * @param tripId 조회할 여행의 ID
     * @return 해당 여행의 총 사용 금액. 결제 내역이 없으면 null을 반환할 수 있음.
     */
    Integer getTotalAmountByTripId(Long tripId);

    /**
     * 2. 특정 여행의 멤버별 총 결제 금액 목록을 조회함.
     *
     * @param tripId 조회할 여행의 ID
     * @return 멤버별 결제 정보 DTO 리스트
     */
    List<SettlementDTO.MemberPaymentInfo> getMemberPaymentsByTripId(Long tripId);

    /**
     * 3. 특정 여행의 멤버별 총 부담 금액 목록을 조회함.
     *
     * @param tripId 조회할 여행의 ID
     * @return 멤버별 부담 정보 DTO 리스트
     */
    List<SettlementDTO.MemberPaymentInfo> getMemberDuesByTripId(Long tripId);

    /**
     * 4. 정산 계산에 필요한 원본 데이터를 조회함.
     * @param tripId 조회할 여행의 ID
     * @return 각 결제 참여 건에 대한 상세 정보(결제자 닉네임, 참여자 닉네임, 분담액) 목록
     */
    List<SettlementDTO.RawSettlementDataDTO> getRawSettlementDataByTripId(Long tripId);
}
