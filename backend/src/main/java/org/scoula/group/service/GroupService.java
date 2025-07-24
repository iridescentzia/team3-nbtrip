package org.scoula.group.service;


import org.scoula.group.dto.GroupDTO;
import org.scoula.group.dto.GroupMemberDTO;

import java.util.List;


public interface GroupService {
    GroupDTO get(int groupId);
    List<GroupMemberDTO> getGroupMembers(int groupId);
    List<GroupDTO> getJoinedGroups(int userId);
    GroupDTO inviteMember(int groupId, int userId);
    int joinGroup(int groupId, int userId);
    int changeMemberStatus(int groupId, int userId);
    int changeGroupStatus(int groupId);
    GroupDTO createGroup(GroupDTO groupDTO);
    boolean isOwner(int groupId, int userId);
}
