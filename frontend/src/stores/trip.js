import { defineStore } from 'pinia';
import { ref, computed } from 'vue';
import tripApi from '@/api/tripApi'; // 전체 default 객체로 불러오기
import memberApi from '@/api/memberApi';

export const useTripStore = defineStore('trip', () => {
  const trips = ref([]);

  // 현재 tripApi는 userId = 1인 trip만 불러옴
  const fetchTrips = async () => {
    const data = await tripApi.fetchTrips();
    console.log('[tripApi.fetchTrips] 응답 데이터:', data);
    trips.value = Array.isArray(data) ? data : [];
  };

  const currentTrip = computed(() => {
    // 대한민국 시간 기준으로 오늘 날짜 00:00:00 생성
    const now = new Date();
    const todayKST = new Date(
      now.toLocaleString('en-US', { timeZone: 'Asia/Seoul' })
    );
    todayKST.setHours(0, 0, 0, 0); // KST 기준 오늘 00:00:00

    // 배열 반환
    return trips.value.find((trip) => {
      if (!trip.startDate || !trip.endDate) return false;

      const [sy, sm, sd] = trip.startDate;
      const [ey, em, ed] = trip.endDate;

      const start = new Date(sy, sm - 1, sd);
      const end = new Date(ey, em - 1, ed);

      return (
        trip.tripStatus === 'ACTIVE' &&
        todayKST.getTime() >= start.getTime() &&
        todayKST.getTime() <= end.getTime()
      );
    });
  });

  const currentTripMembers = ref([]);

  const fetchCurrentTripMembers = async () => {
    if (!currentTrip.value) return;

    try {
      const detail = await tripApi.getTripDetail(currentTrip.value.tripId);
      console.log('getTripDetail: ', detail);
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
        const user = await memberApi.getUserInfo(id);
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
