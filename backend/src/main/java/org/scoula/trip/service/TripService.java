package org.scoula.trip.service;


import org.scoula.trip.dto.TripDTO;
import org.scoula.trip.dto.TripMemberDTO;

import java.util.List;


public interface TripService {
    TripDTO get(int tripId);
    List<TripMemberDTO> getTripMembers(int tripId);
    List<TripDTO> getJoinedTrips(int userId);
    TripDTO inviteMember(int tripId, int userId);
    int joinTrip(int tripId, int userId);
    int changeMemberStatus(int tripId, int userId);
    int changeTripStatus(int tripId);
    TripDTO createTrip(TripDTO tripDTO);
    boolean isOwner(int tripId, int userId);
}
