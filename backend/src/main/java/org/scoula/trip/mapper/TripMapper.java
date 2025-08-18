package org.scoula.trip.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.scoula.trip.domain.TripMemberStatus;
import org.scoula.trip.domain.TripMemberVO;
import org.scoula.trip.domain.TripVO;

import java.time.LocalDate;
import java.util.List;

@Mapper
public interface TripMapper {
    /**
     * 1. 특정 여행에 참여한 모든 멤버의 닉네임을 조회함.
     *
     * @param tripId 조회할 여행의 ID
     * @return 해당 여행의 멤버 닉네임 리스트
     */
    List<Integer> findUserIdsByTripId(int tripId);
    List<String> findNicknamesByTripId(int tripId);
    TripVO getTripDetail(int tripId);
    List<TripMemberVO> getTripMembers(int tripId);
    List<TripVO> getJoinedTrips(int userId);
    List<TripVO> getJoinedTripDates(int userId);
    void createTrip(TripVO tripVO);
    int joinTrip(@Param("tripId") int tripId, @Param("userId") int userId);
    int changeMemberStatus(@Param("tripId") int tripId, @Param("userId") int userId, @Param("status") TripMemberStatus status);
    void inviteTrip(@Param("tripId") int tripId, @Param("userId") int userId, @Param("status")TripMemberStatus status);
    int changeTripStatus(int tripId);
    boolean isOwner(@Param("tripId") int tripId, @Param("userId") int userId);
    /**
     * 여행 ID로 여행 이름 조회
     * @param tripId 여행 ID
     * @return 여행 이름
     */
    String findTripNameById(int tripId);
    // 본인이 참가한 상태별 여행 조회
    List<TripVO> getTripsByStatus(@Param("userId") int userId, @Param("status") String status);
    int updateTrip(TripVO tripVO);
    int deleteTrip(int tripId);
    TripMemberStatus getMemberStatus(@Param("tripId") int tripId, @Param("userId") int userId);
    int activateTrip(@Param("today") LocalDate today);
}
