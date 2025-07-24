package org.scoula.group.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GroupMemberVO {
    private int memberId;
    private int userId;
    private int groupId;
    private GroupMemberStatus memberStatus;
}
