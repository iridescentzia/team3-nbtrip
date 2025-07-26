package org.scoula.group.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.scoula.group.domain.GroupMemberVO;
import org.scoula.group.domain.GroupVO;
import org.scoula.group.domain.GroupStatus;

import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GroupDTO {
    private int groupId;
    private int ownerId;
    private String groupName;
    private Date startDate;
    private Date endDate;
    private int budget;
    private GroupStatus groupStatus;
    List<GroupMemberVO> members;

    public static GroupDTO of(GroupVO groupVO) {
        return GroupDTO.builder()
                .groupId(groupVO.getGroupId())
                .ownerId(groupVO.getOwnerId())
                .groupName(groupVO.getGroupName())
                .startDate(groupVO.getStartDate())
                .endDate(groupVO.getEndDate())
                .budget(groupVO.getBudget())
                .groupStatus(groupVO.getGroupStatus())
                .members(groupVO.getMembers())
                .build();
    }
    public static GroupVO toVO(GroupDTO dto) {
        return GroupVO.builder()
                .groupId(dto.groupId)
                .ownerId(dto.ownerId)
                .groupName(dto.groupName)
                .startDate(dto.startDate)
                .endDate(dto.endDate)
                .budget(dto.budget)
                .groupStatus(dto.groupStatus)
                .members(dto.members)
                .build();
    }
}
