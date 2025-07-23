package org.scoula.group.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GroupVO {
    private int trip_id;
    private int owner_id;
    private String trip_name;
    private Date start_date;
    private Date end_date;
    private int budget;
    private TripStatus trip_status;
}
