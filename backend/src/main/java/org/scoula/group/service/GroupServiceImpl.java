package org.scoula.group.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.scoula.group.domain.GroupVO;
import org.scoula.group.dto.GroupCreateDTO;
import org.scoula.group.mapper.GroupMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Log4j2
@Service
@RequiredArgsConstructor
public class GroupServiceImpl implements GroupService {
    final GroupMapper mapper;

    @Override
    public GroupCreateDTO get(int id) {
        GroupVO groupVO = Optional.ofNullable(mapper.getGroupsById(id))
                .orElseThrow(NoSuchElementException::new);
        return GroupCreateDTO.of(groupVO);
    }

    @Override
    public List<GroupCreateDTO> getOnesGroups(int ownerId) {
        List<GroupVO> ownGroups = mapper.getGroupsByOwnerId(ownerId);
        return ownGroups.stream().map(GroupCreateDTO::of).toList();
    }
}
