package org.scoula.trip.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.scoula.trip.domain.TripMemberVO;
import org.scoula.trip.domain.TripVO;
import org.scoula.trip.domain.TripStatus;

import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TripDTO {
    private int tripId;
    private int ownerId;
    private String tripName;
    private Date startDate;
    private Date endDate;
    private int budget;
    private TripStatus tripStatus;
    List<TripMemberVO> members;

    public static TripDTO of(TripVO tripVO) {
        return TripDTO.builder()
                .tripId(tripVO.getTripId())
                .ownerId(tripVO.getOwnerId())
                .tripName(tripVO.getTripName())
                .startDate(tripVO.getStartDate())
                .endDate(tripVO.getEndDate())
                .budget(tripVO.getBudget())
                .tripStatus(tripVO.getTripStatus())
                .members(tripVO.getMembers())
                .build();
    }
    public static TripVO toVO(TripDTO dto) {
        return TripVO.builder()
                .tripId(dto.tripId)
                .ownerId(dto.ownerId)
                .tripName(dto.tripName)
                .startDate(dto.startDate)
                .endDate(dto.endDate)
                .budget(dto.budget)
                .tripStatus(dto.tripStatus)
                .members(dto.members)
                .build();
    }
}
