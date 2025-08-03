import { defineStore } from 'pinia';
import { ref, computed } from 'vue';
import tripApi from '@/api/tripApi'; // 전체 default 객체로 불러오기
import memberApi from '@/api/memberApi';

export const useTripStore = defineStore('trip', () => {
  const trips = ref([]);

  const fetchTrips = async () => {
    const data = await tripApi.fetchTrips();
    trips.value = Array.isArray(data) ? data : [];
  };

  const currentTrip = computed(() => {
    const now = Date.now();
    return trips.value.find((trip) => {
      return (
        trip.tripStatus === 'ACTIVE' &&
        now >= trip.startDate &&
        now <= trip.endDate
      );
    });
  });

  const currentTripMembers = ref([]);

  const fetchCurrentTripMembers = async () => {
    if (!currentTrip.value) return;

    try {
      const detail = await tripApi.getTripDetail(currentTrip.value.tripId);
      console.log(detail);
      currentTripMembers.value = Array.isArray(detail.members)
        ? detail.members
        : [];
    } catch (error) {
      console.error('Failed to fetch current trip members:', error);
      currentTripMembers.value = [];
    }
  };

  const currentTripMemberNicknames = ref([]);

  const fetchCurrentTripMemberNicknames = async () => {
    currentTripMemberNicknames.value = [];

    // currentTripMembers는 이미 tripDetail에서 가져온 상태여야 함
    const memberIds = currentTripMembers.value.map((m) => m.userId);

    const nicknamePromises = memberIds.map(async (id) => {
      try {
        const user = await memberApi.get(id);
        return { userId: id, nickname: user.nickname };
      } catch (error) {
        console.error(`Failed to fetch user ${id}:`, error);
        return { userId: id, nickname: null };
      }
    });

    currentTripMemberNicknames.value = await Promise.all(nicknamePromises);
  };

  return {
    trips,
    fetchTrips,
    currentTrip,
    fetchCurrentTripMembers,
    currentTripMembers,
    fetchCurrentTripMemberNicknames,
    currentTripMemberNicknames,
  };
});
