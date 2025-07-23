package org.scoula.group.service;


import org.scoula.group.dto.GroupCreateDTO;

import java.util.List;

public interface GroupService {
    GroupCreateDTO get(int id);
    List<GroupCreateDTO> getOnesGroups(int ownerId);
}
