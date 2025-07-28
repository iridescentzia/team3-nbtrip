package org.scoula.group.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.scoula.group.domain.GroupMemberVO;
import org.scoula.group.domain.GroupVO;
import java.util.List;

@Mapper
public interface GroupMapper {
    /**
     * 1. 특정 여행에 참여한 모든 멤버의 닉네임을 조회함.
     *
     * @param tripId 조회할 여행의 ID
     * @return 해당 여행의 멤버 닉네임 리스트
     */
    List<String> findNicknamesByTripId(int tripId);
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
