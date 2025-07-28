package org.scoula.trip.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.scoula.trip.domain.TripMemberVO;
import org.scoula.trip.domain.TripVO;
import java.util.List;

@Mapper
public interface TripMapper {
    /**
     * 1. 특정 여행에 참여한 모든 멤버의 닉네임을 조회함.
     *
     * @param tripId 조회할 여행의 ID
     * @return 해당 여행의 멤버 닉네임 리스트
     */
    List<String> findNicknamesByTripId(int tripId);
    TripVO getTripDetail(int tripId);
    List<TripMemberVO> getTripMembers(int tripId);
    List<TripVO> getJoinedTrips(int userId);
    void createTrip(TripVO tripVO);
    int joinTrip(@Param("tripId") int tripId, @Param("userId") int userId);
    int changeMemberStatus(@Param("tripId") int tripId, @Param("userId") int userId);
    void inviteTrip(@Param("tripId") int tripId, @Param("userId") int userId);
    int changeTripStatus(int tripId);
    boolean isOwner(@Param("tripId") int tripId, @Param("userId") int userId);
}
