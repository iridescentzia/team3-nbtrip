package org.scoula.settlement.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.scoula.settlement.domain.SettlementVO;
import org.scoula.settlement.dto.SettlementDTO;
import java.util.List;
import java.util.Map;

@Mapper // mybatis 정산 매퍼, SettlementMapper.xml과 연동하여 sql 실행
public interface SettlementMapper {

    // 특정 유저가 참여한 모든 정산 내역 조회 (닉네임 없음)
    public List<SettlementVO> getSettlementsByUserId(@Param("userId") int userId);

    // 특정 유저가 참여한 모든 정산 내역 조회 (JOIN - 닉네임 포함)
    List<SettlementVO> getSettlementsWithNicknamesByUserId(@Param("userId") int userId);

    // 특정 여행의 정산 내역 조회 (닉네임 없음)
    List<SettlementVO> getSettlementsByTripId(@Param("tripId") int tripId);

    // 특정 여행의 정산 내역 조회 (JOIN - 닉네임포함)
    List<SettlementVO> getSettlementsWithNicknamesByTripId(@Param("tripId") int tripId);

    // 상태별 정산 내역 필터링 조회 : pending, processing, completed
    public List<SettlementVO> getSettlementsByStatus(@Param("status") String status);

    // 정산 상태 업데이트 : pending -> processing
    public int updateSettlementStatus(@Param("settlementId") int settlementId, @Param("newStatus") String newStatus);

    // 정산 ID로 단건 조회 : 상태 확인 또는 상세 조회용
    public SettlementVO getById(@Param("settlementId") int settlementId);

    // 정산 ID로 단건 조회 (JOIN - 닉네임 포함)
    SettlementVO getByIdWithNicknames(@Param("settlementId") int settlementId);

    // 특정 여행 내 아직 완료되지 않은 정산 건 존재 여부 확인용
    public List<SettlementVO> getPendingOrProcessingByTripId(@Param("tripId") int tripId);

    // DB insert용
    public void insertSettlement(@Param("vo") SettlementVO vo);
  
    
    /**
     * 1. 특정 여행의 총 사용 금액을 조회함.
     *
     * @param tripId 조회할 여행의 ID
     * @return 해당 여행의 총 사용 금액. 결제 내역이 없으면 null을 반환할 수 있음.
     */
    Integer getTotalAmountByTripId(@Param("tripId") int tripId);


    /**
     * 2. 특정 여행의 멤버별 총 결제 금액 목록을 조회함.
     *
     * @param tripId 조회할 여행의 ID
     * @return 멤버별 결제 정보 DTO 리스트
     */
    List<SettlementDTO.MemberPaymentInfo> getMemberPaymentsByTripId(@Param("tripId") int tripId);

    /**
     * 3. 특정 여행의 멤버별 총 부담 금액 목록을 조회함.
     *
     * @param tripId 조회할 여행의 ID
     * @return 멤버별 부담 정보 DTO 리스트
     */
    List<SettlementDTO.MemberPaymentInfo> getMemberDuesByTripId(@Param("tripId") int tripId);

    /**
     * 4. 정산 계산에 필요한 원본 데이터를 조회함.
     * @param tripId 조회할 여행의 ID
     * @return 각 결제 참여 건에 대한 상세 정보(결제자 닉네임, 참여자 닉네임, 분담액) 목록
     */
    List<SettlementDTO.RawSettlementDataDTO> getRawSettlementDataByTripId(@Param("tripId") int tripId);

    /**
     * [NEW] 특정 여행의 settlement 테이블에 저장된 모든 정산 내역을 조회함.
     * @param tripId 조회할 여행의 ID
     * @return 최종 송금 거래 DTO 리스트
     */
    List<SettlementDTO.OptimizedNicknameTransaction> getNicknameSettlementsByTripId(int tripId);
}
