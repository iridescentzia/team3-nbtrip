package org.scoula.trip.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TripVO {
    private int tripId;
    private int ownerId;
    private String tripName;
    private LocalDate startDate;
    private LocalDate endDate;
    private int budget;
    private TripStatus tripStatus;
    private List<TripMemberVO> members;
}
