package org.scoula.trip.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TripMemberVO {
    private int memberId;
    private int userId;
    private int tripId;
    private TripMemberStatus memberStatus;
}
