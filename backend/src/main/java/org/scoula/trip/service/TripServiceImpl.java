package org.scoula.trip.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.scoula.trip.domain.TripMemberVO;
import org.scoula.trip.domain.TripVO;
import org.scoula.trip.dto.TripDTO;
import org.scoula.trip.dto.TripMemberDTO;
import org.scoula.trip.mapper.TripMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Log4j2
@Service
@RequiredArgsConstructor
public class TripServiceImpl implements TripService {
    final TripMapper mapper;

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
    public int joinTrip(int tripId, int userId) {
        return mapper.joinTrip(tripId, userId);
    }

    @Override
    public TripDTO inviteMember(int tripId, int userId) {
        mapper.inviteTrip(tripId, userId);
        return get(tripId);
    }

    @Override
    public int changeMemberStatus(int tripId, int userId) {
        return mapper.changeMemberStatus(tripId, userId);
    }

    @Override
    public int changeTripStatus(int tripId) {
        return mapper.changeTripStatus(tripId);
    }

    @Override
    public boolean isOwner(int tripId, int userId) {
        return mapper.isOwner(tripId, userId);
    }

    @Transactional
    @Override
    public TripDTO createTrip(TripDTO tripDTO) {
        TripVO tripVO = TripDTO.toVO(tripDTO);
        mapper.createTrip(tripVO);
        List<TripMemberVO> tripMembers = tripVO.getMembers();
        for (TripMemberVO tripMemberVO : tripMembers) {
            inviteMember(tripVO.getTripId(), tripMemberVO.getUserId());
        }
        return get(tripVO.getTripId());
    }
}
