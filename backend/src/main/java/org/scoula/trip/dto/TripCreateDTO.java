package org.scoula.trip.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.scoula.member.dto.MemberSearchResponseDTO;
import org.scoula.trip.domain.TripMemberVO;
import org.scoula.trip.domain.TripStatus;
import org.scoula.trip.domain.TripVO;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TripCreateDTO {
    private int ownerId;
    private String tripName;
    private LocalDate startDate;
    private LocalDate endDate;
    private int budget;
    private TripStatus tripStatus;
    List<MemberSearchResponseDTO> members;

    public static TripVO toVO(TripCreateDTO tripCreateDTO) {
        return TripVO.builder()
                .ownerId(tripCreateDTO.ownerId)
                .tripName(tripCreateDTO.tripName)
                .startDate(tripCreateDTO.startDate)
                .endDate(tripCreateDTO.endDate)
                .budget(tripCreateDTO.budget)
                .tripStatus(tripCreateDTO.tripStatus)
                .build();
    }
}
