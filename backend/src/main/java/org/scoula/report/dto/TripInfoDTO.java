package org.scoula.report.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TripInfoDTO {
    private String tripName;
    private String startDate;
    private String endDate;
}
