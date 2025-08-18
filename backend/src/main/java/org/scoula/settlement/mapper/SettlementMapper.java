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

    /**
     * 특정 사용자의 특정 여행 정산 내역 조회 (송금/수신 모두 포함)
     * @param userId 사용자 ID
     * @param tripId 여행 ID
     * @return 해당 사용자가 관련된 정산 내역 리스트
     */
    List<SettlementVO> getMySettlementsByTripId(@Param("userId") int userId, @Param("tripId") int tripId);

    /**
     * 특정 사용자가 특정 여행에서 보내야 할 정산들만 조회
     * @param userId 사용자 ID
     * @param tripId 여행 ID
     * @return 송금해야 할 정산 리스트
     */
    List<SettlementVO> getMyOutgoingSettlementsByTripId(@Param("userId") int userId, @Param("tripId") int tripId);

    /**
     * 특정 사용자가 특정 여행에서 받아야 할 정산들만 조회
     * @param userId 사용자 ID
     * @param tripId 여행 ID
     * @return 받아야 할 정산 리스트
     */
    List<SettlementVO> getMyIncomingSettlementsByTripId(@Param("userId") int userId, @Param("tripId") int tripId);

    /**
     * 사용자별 정산 상태 통계 조회
     * @param userId 사용자 ID
     * @return 상태별 카운트가 포함된 DTO
     */
    SettlementDTO.MySettlementStatusResponseDto getMySettlementStatusCounts(@Param("userId") int userId);

    /**
     * 여행별 정산 상태 통계 조회
     * @param tripId 여행 ID
     * @return 상태별 카운트가 포함된 DTO
     */
    SettlementDTO.RemainingSettlementResponseDto getRemainingSettlementCounts(@Param("tripId") int tripId);

    /**
     * 특정 사용자의 미정산 여행 목록을 조회함.
     * @param userId 사용자의 ID
     * @return 미정산 여행 정보 DTO 리스트
     */
    List<SettlementDTO.UnsettledTripInfo> findUnsettledTripsByUserId(@Param("userId") int userId);

    /**
     * [NEW] 두 사용자 간의 정산 과정 상세 내역을 조회함.
     *
     * @param tripId      조회할 여행의 ID
     * @param myUserId    현재 로그인한 사용자의 ID
     * @param otherUserId 상대방 사용자의 ID
     * @return 두 사용자 간의 모든 거래 상세 정보 목록
     */
    List<SettlementDTO.BreakdownDetailDTO> findBreakdownDetails(
            @Param("tripId") int tripId,
            @Param("myUserId") int myUserId,
            @Param("otherUserId") int otherUserId
    );

    /**
     * 정산 데이터를 배타적 락과 함께 조회
     * @param settlementId 정산 ID
     * @return 정산 정보 (트랜잭션 종료까지 락 유지)
     */
    SettlementVO selectForUpdateWithNicknames(@Param("settlementId") int settlementId);
}
