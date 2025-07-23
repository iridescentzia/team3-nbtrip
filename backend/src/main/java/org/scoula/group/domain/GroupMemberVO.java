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
    private int member_id;
    private int user_id;
    private int trip_id;
    private GroupMemberStatus member_status;
}
