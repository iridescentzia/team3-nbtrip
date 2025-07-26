package org.scoula.group.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.scoula.group.domain.GroupMemberStatus;
import org.scoula.group.domain.GroupMemberVO;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GroupMemberDTO {
    private int memberId;
    private int userId;
    private int groupId;
    private GroupMemberStatus memberStatus;

    public static GroupMemberDTO of(GroupMemberVO groupMemberVO) {
        return GroupMemberDTO
                .builder()
                .memberId(groupMemberVO.getMemberId())
                .userId(groupMemberVO.getUserId())
                .groupId(groupMemberVO.getGroupId())
                .memberStatus(groupMemberVO.getMemberStatus())
                .build();
    }

    public static GroupMemberVO toVO(GroupMemberDTO groupMemberDTO) {
        return GroupMemberVO.builder()
                .memberId(groupMemberDTO.memberId)
                .userId(groupMemberDTO.userId)
                .groupId(groupMemberDTO.groupId)
                .memberStatus(groupMemberDTO.memberStatus)
                .build();
    }
}
