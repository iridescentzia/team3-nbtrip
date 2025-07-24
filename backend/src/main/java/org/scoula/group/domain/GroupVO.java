package org.scoula.group.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GroupVO {
    private int groupId;
    private int ownerId;
    private String groupName;
    private Date startDate;
    private Date endDate;
    private int budget;
    private GroupStatus groupStatus;
    private List<GroupMemberVO> members;
}
