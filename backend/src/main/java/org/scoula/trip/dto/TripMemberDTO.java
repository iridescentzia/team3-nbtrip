package org.scoula.trip.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.scoula.trip.domain.TripMemberStatus;
import org.scoula.trip.domain.TripMemberVO;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TripMemberDTO {
    private int memberId;
    private int userId;
    private int tripId;
    private TripMemberStatus memberStatus;

    public static TripMemberDTO of(TripMemberVO tripMemberVO) {
        return TripMemberDTO
                .builder()
                .memberId(tripMemberVO.getMemberId())
                .userId(tripMemberVO.getUserId())
                .tripId(tripMemberVO.getTripId())
                .memberStatus(tripMemberVO.getMemberStatus())
                .build();
    }

    public static TripMemberVO toVO(TripMemberDTO tripMemberDTO) {
        return TripMemberVO.builder()
                .memberId(tripMemberDTO.memberId)
                .userId(tripMemberDTO.userId)
                .tripId(tripMemberDTO.tripId)
                .memberStatus(tripMemberDTO.memberStatus)
                .build();
    }
}
