package org.scoula.trip.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.scoula.member.dto.MemberSearchResponseDTO;
import org.scoula.trip.domain.TripStatus;
import org.scoula.trip.domain.TripVO;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class TripUpdateDTO {
    private int tripId;
    private String tripName;
    private LocalDate startDate;
    private LocalDate endDate;
    private TripStatus tripStatus;
    List<TripMemberDTO> members;

    public static TripVO toVO(TripUpdateDTO tripUpdateDTO) {
        return TripVO.builder()
                .tripId(tripUpdateDTO.getTripId())
                .tripName(tripUpdateDTO.getTripName())
                .startDate(tripUpdateDTO.getStartDate())
                .endDate(tripUpdateDTO.getEndDate())
                .tripStatus(tripUpdateDTO.getTripStatus())
                .build();
    }
}
