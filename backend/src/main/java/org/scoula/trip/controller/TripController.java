package org.scoula.trip.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.scoula.trip.dto.TripDTO;
import org.scoula.trip.dto.TripMemberDTO;
import org.scoula.trip.service.TripService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@Log4j2
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/trips")
public class TripController {
    final TripService service;
    //그룹 1개의 정보(멤버 리스트 포함)를 그룹 ID로 찾아 가져오기
    @GetMapping("/{tripId}")
    public ResponseEntity<TripDTO> getTripByID(@PathVariable int tripId) {
        return ResponseEntity.ok().body(service.get(tripId));
    }

    //그룹 멤버 데려오기
    @GetMapping("/{tripId}/members")
    public ResponseEntity<List<TripMemberDTO>> getTripMembers(@PathVariable int tripId) {
        return ResponseEntity.ok().body(service.getTripMembers(tripId));
    }

    //Security 구현 전이라 로그인한 유저는 1번이라 가정
    @GetMapping("/{tripId}/isOwner")
    public ResponseEntity<Boolean> isOwner(@PathVariable int tripId) {
        return ResponseEntity.ok().body(service.isOwner(tripId, 1));
    }
    //유저가 참여중인 여행 리스트 유저 ID로 찾아 가져오기
    //로그인 구현 전이라 임시로 1번의 정보를 가져오도록 구현
    @GetMapping("")
    public ResponseEntity<List<TripDTO>> getJoinedTripList() {
        return ResponseEntity.ok().body(service.getJoinedTrips(1));
    }
    //그룹 ID인 그룹에 유저 ID인 유저 초대하기
    @PostMapping("/{tripId}/invite/{userId}")
    public ResponseEntity<TripDTO> joinTripMembers(@PathVariable int userId, @PathVariable int tripId) {
        return ResponseEntity.ok().body(service.inviteMember(tripId, userId));
    }
    //그룹 ID인 그룹에 참여하기
    @PutMapping("/{tripId}/join")
    public ResponseEntity<Integer> joinTrip(@PathVariable int tripId){
        return ResponseEntity.ok().body(service.joinTrip(tripId, 2));
    }
    //그룹 ID인 그룹에 유저 ID인 유저의 상태 LEFT로 변경하기
    @PutMapping("/{tripId}/members/{userId}/status")
    public ResponseEntity<Integer> changeMemberStatus(@PathVariable int tripId, @PathVariable int userId){
       return ResponseEntity.ok().body(service.changeMemberStatus(tripId, userId));
    }
    //그룹 ID인 그룹의 상태 CLOSED로 변경하기
    @PutMapping("/{tripId}/status")
    public ResponseEntity<Integer> changeMemberStatus(@PathVariable int tripId){
        return ResponseEntity.ok().body(service.changeTripStatus(tripId));
    }
    @PostMapping("")
    public ResponseEntity<TripDTO> createTrip(TripDTO tripDTO) {
//        TripDTO testDTO = TripDTO.builder()
//                .ownerId(2)
//                .tripName("테스트 여행명")
//                .startDate(new Date())
//                .endDate(new Date())
//                .tripStatus(TripStatus.ACTIVE)
//                .budget(100000)
//                .members(service.getTripMembers(2).stream().map(TripMemberDTO::toVO).toList())
//                .build();
        return ResponseEntity.ok().body(service.createTrip(tripDTO));
    }
}
