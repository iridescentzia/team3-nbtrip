package org.scoula.trip.service;


import org.scoula.trip.domain.TripMemberStatus;
import org.scoula.trip.domain.TripStatus;
import org.scoula.trip.dto.*;

import java.util.List;


public interface TripService {
    TripDTO get(int tripId);
    List<TripMemberDTO> getTripMembers(int tripId);
    List<TripDTO> getJoinedTrips(int userId);
    List<TripDatesDTO> getJoinedTripDates(int userId);
    TripDTO inviteMember(int tripId, int userId, TripMemberStatus status);
    int joinTrip(int tripId, int userId);
    int changeMemberStatus(int tripId, int userId, TripMemberStatus status);
    int changeTripStatus(int tripId);
    TripDTO createTrip(TripCreateDTO tripCreateDTO);
    boolean isOwner(int tripId, int userId);
    boolean isMember(int tripId, int userId, boolean checkJoined);
    List<TripDTO> getTripsByStatus(int userId, TripStatus status);
    TripDTO updateTrip(TripUpdateDTO tripUpdateDTO);
    int deleteTrip(int tripId);
    TripMemberStatus getMemberStatus(int tripId, int userId);
}
