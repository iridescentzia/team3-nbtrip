package org.scoula.group.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.scoula.group.domain.GroupMemberVO;
import org.scoula.group.domain.GroupVO;
import org.scoula.group.dto.GroupDTO;
import org.scoula.group.dto.GroupMemberDTO;
import org.scoula.group.mapper.GroupMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Log4j2
@Service
@RequiredArgsConstructor
public class GroupServiceImpl implements GroupService {
    final GroupMapper mapper;

    @Override
    public GroupDTO get(int groupId) {
        GroupVO groupVO = Optional.ofNullable(mapper.getGroupsDetail(groupId))
                .orElseThrow(NoSuchElementException::new);
        return GroupDTO.of(groupVO);
    }

    @Override
    public List<GroupMemberDTO> getGroupMembers(int groupId) {
        List<GroupMemberVO> members = Optional.ofNullable(mapper.getGroupMembers(groupId))
                .orElseThrow(NoSuchElementException::new);
        return members.stream().map(GroupMemberDTO::of).toList();
    }

    @Override
    public List<GroupDTO> getJoinedGroups(int userId) {
        List<GroupVO> groupVO= mapper.getJoinedGroups(userId);
        return groupVO.stream().map(GroupDTO::of).toList();
    }

    @Override
    public int joinGroup(int groupId, int userId) {
        return mapper.joinGroup(groupId, userId);
    }

    @Override
    public GroupDTO inviteMember(int groupId, int userId) {
        mapper.inviteGroup(groupId, userId);
        return get(groupId);
    }

    @Override
    public int changeMemberStatus(int groupId, int userId) {
        return mapper.changeMemberStatus(groupId, userId);
    }

    @Override
    public int changeGroupStatus(int groupId) {
        return mapper.changeGroupStatus(groupId);
    }

    @Override
    public boolean isOwner(int groupId, int userId) {
        return mapper.isOwner(groupId, userId);
    }

    @Transactional
    @Override
    public GroupDTO createGroup(GroupDTO groupDTO) {
        GroupVO groupVO = GroupDTO.toVO(groupDTO);
        mapper.createGroup(groupVO);
        List<GroupMemberVO> groupMembers = groupVO.getMembers();
        for (GroupMemberVO groupMemberVO : groupMembers) {
            inviteMember(groupVO.getGroupId(), groupMemberVO.getUserId());
        }
        return get(groupVO.getGroupId());
    }
}
