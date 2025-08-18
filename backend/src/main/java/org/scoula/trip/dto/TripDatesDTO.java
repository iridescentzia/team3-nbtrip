package org.scoula.trip.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.scoula.trip.domain.TripVO;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TripDatesDTO {
    private int tripId;
    private LocalDate startDate;
    private LocalDate endDate;

    public static TripDatesDTO of(TripVO tripVO) {
        return TripDatesDTO.builder()
                .tripId(tripVO.getTripId())
                .startDate(tripVO.getStartDate())
                .endDate(tripVO.getEndDate())
                .build();
    }
}
