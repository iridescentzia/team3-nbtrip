package org.scoula.group.mapper;

import org.scoula.group.domain.GroupVO;

import java.util.List;

public interface GroupMapper {
    GroupVO getGroupsById(int id);
    List<GroupVO> getGroupsByOwnerId(int ownerId);
}
