import { defineStore } from 'pinia';
import { ref, computed } from 'vue';
import tripApi from '@/api/tripApi'; 
import memberApi from '@/api/memberApi';
import merchantApi from '@/api/merchantApi';

export const useTripStore = defineStore('trip', () => {
  const trips = ref([]);
  const currentTripMembers = ref([]); // 현재 여행 멤버 목록
  const merchantCategories = ref([]);

  // 현재 tripApi는 userId = 1인 trip만 불러옴
  // 여행 목록 불러오기
  const fetchTrips = async () => {
    const data = await tripApi.fetchTrips();
    console.log('[tripApi.fetchTrips] 응답 데이터:', data);
    trips.value = Array.isArray(data) ? data : [];
  };

  const parseKSTDate = (dateStr) => {
    const [year, month, day] = dateStr.split('-').map(Number);
    // Date.UTC → 한국 시간 00:00으로 세팅
     return new Date(year, month - 1, day, 0, 0, 0);; // UTC+9 대응
  };

  // 현재 진행 중인 여행
  const currentTrip = computed(() => {
    const now = new Date();
    const todayKST = new Date(
      now.toLocaleString('en-US', { timeZone: 'Asia/Seoul' })
    );
    todayKST.setHours(0, 0, 0, 0);

    return trips.value.find((trip) => {
      if (!trip.startDate || !trip.endDate) return false;

      // 문자열 → Date 객체로 파싱
      const start = parseKSTDate(trip.startDate);
      const end = parseKSTDate(trip.endDate);

      return (
        trip.tripStatus === 'ACTIVE' &&
        todayKST >= start &&
        todayKST <= end
      );
    });
});

// 여행 상세 정보에서 member.userId 목록을 받아오고 
// getUserInfo()로 닉네임 붙이기
const fetchCurrentTripMemberNicknames = async () => {
  if (!currentTrip.value) return;

  try{
    // 진행 중 여행 상세 정보 조회
    const detail = await tripApi.getTripDetail(currentTrip.value.tripId)
    const members = Array.isArray(detail.members) ? detail.members : []

    // 각 userId에 대한 닉네임 조회
    const nicknamePromises = members.map(async (member) =>{
      try{
        const user = await memberApi.getUserInfo(member.userId)
        return {
          userId: member.userId,
          nickname: user.nickname
        }
      } catch(error){
        console.error(`userId: ${member.userId} 닉네임 조회 실패`, error)
      } 
    })

    // 완료된 멤버 리스트 저장
    currentTripMembers.value = await Promise.all(nicknamePromises)
  }catch(e){
    console.error('여행 상세 조회 실패: ', e)
    currentTripMembers.value = []
  }
}

// 카테고리 목록 불러오기
const fetchMerchantCategories = async () => {
  try{
    const data = await merchantApi.getAllMerchantCategories()
    merchantCategories.value = data
    console.log("카테고리 목록: ", merchantCategories.value)
  } catch(e) {
    console.error('카테고리 목록 불러오기 실패: ', e)
    merchantCategories.value = []
  }
}

  return {
    trips,
    fetchTrips,
    currentTrip,
    currentTripMembers,
    fetchCurrentTripMemberNicknames,
    fetchMerchantCategories,
    merchantCategories
  };
});