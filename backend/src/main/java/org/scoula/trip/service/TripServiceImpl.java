package org.scoula.trip.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.scoula.member.dto.MemberSearchResponseDTO;
import org.scoula.member.service.MemberService;
import org.scoula.notification.dto.NotificationDTO;
import org.scoula.notification.service.NotificationService;
import org.scoula.trip.domain.TripMemberStatus;
import org.scoula.trip.domain.TripMemberVO;
import org.scoula.trip.domain.TripStatus;
import org.scoula.trip.domain.TripVO;
import org.scoula.trip.dto.*;
import org.scoula.trip.mapper.TripMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Log4j2
@Service
@RequiredArgsConstructor
public class TripServiceImpl implements TripService {
    final TripMapper mapper;
    final NotificationService notificationService;
    final MemberService memberService;

    @Override
    public TripDTO get(int tripId) {
        TripVO tripVO = Optional.ofNullable(mapper.getTripDetail(tripId))
                .orElseThrow(NoSuchElementException::new);
        return TripDTO.of(tripVO);
    }

    @Override
    public List<TripMemberDTO> getTripMembers(int tripId) {
        List<TripMemberVO> members = Optional.ofNullable(mapper.getTripMembers(tripId))
                .orElseThrow(NoSuchElementException::new);
        return members.stream().map(TripMemberDTO::of).toList();
    }

    @Override
    public List<TripDTO> getJoinedTrips(int userId) {
        List<TripVO> tripVO = mapper.getJoinedTrips(userId);
        return tripVO.stream().map(TripDTO::of).toList();
    }

    @Override
    public List<TripDatesDTO> getJoinedTripDates(int userId) {
        return
                mapper.getJoinedTripDates(userId)
                        .stream()
                        .map(TripDatesDTO::of)
                        .toList();
    }

    @Override
    public int joinTrip(int tripId, int userId) {
        int result = mapper.joinTrip(tripId, userId);
        notificationService.createGroupEventNotification(userId, tripId, "JOIN");
        return result;
    }

    @Override
    public TripDTO inviteMember(int tripId, int userId, TripMemberStatus status) {
        mapper.inviteTrip(tripId, userId, status);
        TripDTO result = get(tripId);
        if(status == TripMemberStatus.INVITED) {
            NotificationDTO notificationDTO = NotificationDTO.builder()
                    .userId(userId)
                    .fromUserId(result.getOwnerId())
                    .tripId(result.getTripId())
                    .notificationType("INVITE")
                    .fromUserNickname(memberService.getMemberInfo(result.getOwnerId()).getNickname())
                    .build();
            notificationService.createNotification(notificationDTO);
        }
        return result;
    }

    @Override
    public TripMemberStatus getMemberStatus(int tripId, int userId) {
        return mapper.getMemberStatus(tripId, userId);
    }

    @Override
    public int changeMemberStatus(int tripId, int userId, TripMemberStatus status) {
        TripMemberStatus statusBefore = getMemberStatus(tripId, userId);
        int result = mapper.changeMemberStatus(tripId, userId, status);
        if(statusBefore != status) {
            if (status.name().equals("LEFT")) {
                notificationService.createGroupEventNotification(userId, tripId, "LEFT");
            }
            else{
                notificationService.createGroupEventNotification(userId, tripId, "JOIN");
            }
        }
        return result;
    }

    @Override
    public int changeTripStatus(int tripId) {
        return mapper.changeTripStatus(tripId);
    }

    @Override
    public boolean isOwner(int tripId, int userId) {
        return mapper.isOwner(tripId, userId);
    }

    @Override
    public boolean isMember(int tripId, int userId, boolean checkJoined) {
        List<TripMemberDTO> tripMemberDTOList = getTripMembers(tripId);
        for (TripMemberDTO tripMemberDTO : tripMemberDTOList) {
            if (tripMemberDTO.getUserId() == userId) {
                if(checkJoined) {
                    return tripMemberDTO.getMemberStatus() != TripMemberStatus.INVITED;
                }
                else {
                    return true;
                }
            }
        }
        return false;
    }

    @Transactional
    @Override
    public TripDTO createTrip(TripCreateDTO tripCreateDTO) {
        TripVO tripVO = TripCreateDTO.toVO(tripCreateDTO);
        if(Objects.equals(tripCreateDTO.getStartDate(), LocalDate.now())){
            tripVO.setTripStatus(TripStatus.ACTIVE);
        }
        mapper.createTrip(tripVO);
        inviteMember(tripVO.getTripId(),tripVO.getOwnerId(),TripMemberStatus.JOINED);
        for (MemberSearchResponseDTO memberSearchResponseDTO : tripCreateDTO.getMembers()) {
            inviteMember(tripVO.getTripId(), memberSearchResponseDTO.getUserId(), TripMemberStatus.INVITED);
        }
        return get(tripVO.getTripId());
    }

    @Override
    public List<TripDTO> getTripsByStatus(int userId, TripStatus status) {
        log.info("상태별 여행 목록 조회 - userId: {}, status: {}", userId, status);
        List<TripVO> trips = mapper.getTripsByStatus(userId, status.name());
        log.info("조회된 여행 수: "+ trips.size());
        return trips.stream()
                .map(TripDTO::of)
                .collect(Collectors.toList());
    }

    @Transactional
    @Override
    public TripDTO updateTrip(TripUpdateDTO tripUpdateDTO) {
        log.info("수정된 여행 정보: {}", tripUpdateDTO);

        mapper.updateTrip(TripUpdateDTO.toVO(tripUpdateDTO));

        if (tripUpdateDTO.getMembers() != null && !tripUpdateDTO.getMembers().isEmpty()) {
            for (TripMemberDTO tripMemberDTO : tripUpdateDTO.getMembers()) {
                try {
                    changeMemberStatus(tripMemberDTO.getTripId(), tripMemberDTO.getUserId(), tripMemberDTO.getMemberStatus());
                } catch (Exception e) {
                    log.error("멤버 상태 변경 실패 (tripId={}, userId={}): {}",
                            tripMemberDTO.getTripId(), tripMemberDTO.getUserId(), e.getMessage(), e);
                }
            }
        }
        return get(tripUpdateDTO.getTripId());
    }

    @Override
    public int deleteTrip(int tripId) {
        return mapper.deleteTrip(tripId);
    }

    @Override
    public int activateTrip(LocalDate today) {
        return mapper.activateTrip(today);
    }
}
