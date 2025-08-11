package org.scoula.trip.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.scoula.trip.domain.TripMemberStatus;
import org.scoula.trip.domain.TripStatus;
import org.scoula.trip.dto.TripCreateDTO;
import org.scoula.security.accounting.domain.CustomUser;
import org.scoula.trip.dto.TripDTO;
import org.scoula.trip.dto.TripMemberDTO;
import org.scoula.trip.dto.TripUpdateDTO;
import org.scoula.trip.service.TripService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.List;

@Log4j2
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/trips")
public class TripController {
    final TripService service;

    // 진행 중인 여행 Id 조회
    @GetMapping("/current")
    public ResponseEntity<Integer> getCurrentTripId(@AuthenticationPrincipal CustomUser customUser) {
        Integer userId = customUser.getUserId();
        try {
            List<TripDTO> trips = service.getJoinedTrips(userId);
            TripDTO currentTrip = new TripDTO();
            for(TripDTO trip : trips) {
                if ((trip.getStartDate().isBefore(LocalDate.now()) || trip.getStartDate().isEqual(LocalDate.now())) &&
                        (trip.getEndDate().isAfter(LocalDate.now()) || trip.getEndDate().isEqual(LocalDate.now()))) {

                    currentTrip = trip;
                }
            }
            int tripId = currentTrip.getTripId();
            return ResponseEntity.ok(tripId);
        } catch (RuntimeException e) {
            log.error("currentTripId 없음: {}", e.getMessage());
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(0);
        }
    }
    //여행 생성 전 로그인한 ID 가져오기
    @GetMapping("/getId")
    public ResponseEntity<Integer> getId(@AuthenticationPrincipal CustomUser customUser) {
        if (customUser != null) {
            return ResponseEntity.ok(customUser.getUserId());
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(0);
    }
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

    //자신이 여행을 생성한 사람인지 체크
    @GetMapping("/{tripId}/isOwner")
    public ResponseEntity<Boolean> isOwner(@AuthenticationPrincipal CustomUser customUser, @PathVariable int tripId) {
        return ResponseEntity.ok().body(service.isOwner(tripId, customUser.getUserId()));
    }
    //유저가 참여중인 여행 리스트 유저 ID로 찾아 가져오기
    @GetMapping("")
    public ResponseEntity<List<TripDTO>> getJoinedTripList(@AuthenticationPrincipal CustomUser customUser) {
        return ResponseEntity.ok().body(service.getJoinedTrips(customUser.getUserId()));
    }
    //그룹 ID인 그룹에 유저 ID인 유저 초대하기
    @PostMapping("/{tripId}/invite/{userId}")
    public ResponseEntity<TripDTO> joinTripMembers(@PathVariable int userId, @PathVariable int tripId) {
        return ResponseEntity.ok().body(service.inviteMember(tripId, userId, TripMemberStatus.INVITED));
    }
    //그룹 ID인 그룹에 참여하기
    @PutMapping("/{tripId}/join")
    public ResponseEntity<Integer> joinTrip(@AuthenticationPrincipal CustomUser customUser,@PathVariable int tripId){
        return ResponseEntity.ok().body(service.joinTrip(tripId, customUser.getUserId()));
    }
    //그룹 ID인 그룹에 유저 ID인 유저의 상태 LEFT로 변경하기
    @PutMapping("/{tripId}/members/{userId}/status")
    public ResponseEntity<Integer> changeMemberStatus(@AuthenticationPrincipal CustomUser customUser, @PathVariable int tripId, @PathVariable int userId){

        if(service.isOwner(tripId, customUser.getUserId())){
            return ResponseEntity.ok().body(service.changeMemberStatus(tripId, userId,TripMemberStatus.LEFT));
        }
        else{
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(0);
        }
    }
    //그룹 ID인 그룹의 상태 CLOSED로 변경하기
    @PutMapping("/{tripId}/status")
    public ResponseEntity<Integer> changeTripStatus(@AuthenticationPrincipal CustomUser customUser,@PathVariable int tripId){
        if(service.isOwner(tripId, customUser.getUserId())){
            return ResponseEntity.ok().body(service.changeTripStatus(tripId));
        }
        else{
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(0);
        }
    }
    //tripId인 여행 수정하기
    @PutMapping("/{tripId}/update")
    public ResponseEntity<TripDTO> updateTrip(@AuthenticationPrincipal CustomUser customUser,
                                              @PathVariable int tripId,
                                              @RequestBody TripUpdateDTO tripUpdateDTO){
        tripUpdateDTO.setTripId(tripId); // URL값 우선
        return ResponseEntity.ok().body(service.updateTrip(tripUpdateDTO));
    }
    @DeleteMapping("/{tripId}/delete")
    public ResponseEntity<Integer> deleteTrip(@AuthenticationPrincipal CustomUser customUser, @PathVariable int tripId){
        if(service.isOwner(tripId, customUser.getUserId())){
            return ResponseEntity.ok().body(service.deleteTrip(tripId));
        }
        else
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(0);
    }

    @PostMapping("")
    public ResponseEntity<TripDTO> createTrip(@AuthenticationPrincipal CustomUser customUser, @RequestBody TripCreateDTO tripCreateDTO) {
        tripCreateDTO.setOwnerId(customUser.getUserId());
        return ResponseEntity.ok().body(service.createTrip(tripCreateDTO));
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<TripDTO>> getTripsByStatus(@PathVariable TripStatus status, @AuthenticationPrincipal CustomUser customUser) {
        int userId = customUser.getUserId();
        List<TripDTO> trips = service.getTripsByStatus(userId, status);
        return ResponseEntity.ok(trips);
    }

}
