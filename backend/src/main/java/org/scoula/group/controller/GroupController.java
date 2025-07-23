package org.scoula.group.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.scoula.group.dto.GroupCreateDTO;
import org.scoula.group.service.GroupService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Log4j2
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/group")
public class GroupController {
    final GroupService service;
    @GetMapping("/getgroup/{groupid}")
    public ResponseEntity<GroupCreateDTO> getGroupById(@PathVariable int groupid) {
        return ResponseEntity.ok().body(service.get(groupid));
    }
    @GetMapping("/getown/{ownerid}")
    public ResponseEntity<List<GroupCreateDTO>> getOwnersGroup(@PathVariable int ownerid) {
        return ResponseEntity.ok().body(service.getOnesGroups(ownerid));
    }
}
