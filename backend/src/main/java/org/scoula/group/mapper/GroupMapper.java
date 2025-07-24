package org.scoula.group.mapper;

import org.apache.ibatis.annotations.Param;
import org.scoula.group.domain.GroupMemberVO;
import org.scoula.group.domain.GroupVO;

import java.util.List;

public interface GroupMapper {
    GroupVO getGroupsDetail(int groupId);
    List<GroupMemberVO> getGroupMembers(int groupId);
    List<GroupVO> getJoinedGroups(int userId);
    void createGroup(GroupVO groupVO);
    int joinGroup(@Param("groupId") int groupId, @Param("userId") int userId);
    int changeMemberStatus(@Param("groupId") int groupId, @Param("userId") int userId);
    void inviteGroup(@Param("groupId") int groupId, @Param("userId") int userId);
    int changeGroupStatus(int groupId);
    boolean isOwner(@Param("groupId") int groupId, @Param("userId") int userId);
}
