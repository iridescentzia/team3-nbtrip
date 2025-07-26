package org.scoula.group.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.scoula.group.dto.GroupDTO;
import org.scoula.group.dto.GroupMemberDTO;
import org.scoula.group.service.GroupService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@Log4j2
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/groups")
public class GroupController {
    final GroupService service;
    //그룹 1개의 정보(멤버 리스트 포함)를 그룹 ID로 찾아 가져오기
    @GetMapping("/{groupId}")
    public ResponseEntity<GroupDTO> getGroupById(@PathVariable int groupId) {
        return ResponseEntity.ok().body(service.get(groupId));
    }

    //그룹 멤버 데려오기
    @GetMapping("/{groupId}/members")
    public ResponseEntity<List<GroupMemberDTO>> getGroupMembers(@PathVariable int groupId) {
        return ResponseEntity.ok().body(service.getGroupMembers(groupId));
    }

    //Security 구현 전이라 로그인한 유저는 1번이라 가정
    @GetMapping("/{groupId}/isOwner")
    public ResponseEntity<Boolean> isOwner(@PathVariable int groupId) {
        return ResponseEntity.ok().body(service.isOwner(groupId, 1));
    }
    //유저가 참여중인 여행 리스트 유저 ID로 찾아 가져오기
    //로그인 구현 전이라 임시로 1번의 정보를 가져오도록 구현
    @GetMapping("")
    public ResponseEntity<List<GroupDTO>> getJoinedGroupList() {
        return ResponseEntity.ok().body(service.getJoinedGroups(1));
    }
    //그룹 ID인 그룹에 유저 ID인 유저 초대하기
    @PostMapping("/{groupId}/invite/{userId}")
    public ResponseEntity<GroupDTO> joinGroupMembers(@PathVariable int userId, @PathVariable int groupId) {
        return ResponseEntity.ok().body(service.inviteMember(groupId, userId));
    }
    //그룹 ID인 그룹에 참여하기
    @PutMapping("/{groupId}/join")
    public ResponseEntity<Integer> joinGroup(@PathVariable int groupId){
        return ResponseEntity.ok().body(service.joinGroup(groupId, 2));
    }
    //그룹 ID인 그룹에 유저 ID인 유저의 상태 LEFT로 변경하기
    @PutMapping("/{groupId}/members/{userId}/status")
    public ResponseEntity<Integer> changeMemberStatus(@PathVariable int groupId, @PathVariable int userId){
       return ResponseEntity.ok().body(service.changeMemberStatus(groupId, userId));
    }
    //그룹 ID인 그룹의 상태 CLOSED로 변경하기
    @PutMapping("/{groupId}/status")
    public ResponseEntity<Integer> changeMemberStatus(@PathVariable int groupId){
        return ResponseEntity.ok().body(service.changeGroupStatus(groupId));
    }
    @PostMapping("")
    public ResponseEntity<GroupDTO> createGroup(GroupDTO groupDTO) {
//        GroupDTO testDTO = GroupDTO.builder()
//                .ownerId(2)
//                .groupName("테스트 여행명")
//                .startDate(new Date())
//                .endDate(new Date())
//                .groupStatus(GroupStatus.ACTIVE)
//                .budget(100000)
//                .members(service.getGroupMembers(2).stream().map(GroupMemberDTO::toVO).toList())
//                .build();
        return ResponseEntity.ok().body(service.createGroup(groupDTO));
    }
}
