package org.scoula.group.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.scoula.group.domain.GroupVO;
import org.scoula.group.domain.TripStatus;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GroupCreateDTO {
    private int trip_id;
    private int owner_id;
    private String trip_name;
    private Date start_date;
    private Date end_date;
    private int budget;
    private TripStatus trip_status;

    public static GroupCreateDTO of(GroupVO groupVO) {
        GroupCreateDTO dto = GroupCreateDTO.builder()
                .trip_id(groupVO.getTrip_id())
                .owner_id(groupVO.getOwner_id())
                .trip_name(groupVO.getTrip_name())
                .start_date(groupVO.getStart_date())
                .end_date(groupVO.getEnd_date())
                .budget(groupVO.getBudget())
                .trip_status(groupVO.getTrip_status())
                .build();
        return dto;
    }
}
