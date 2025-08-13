import {defineStore} from 'pinia'
import {ref} from 'vue';
import tripApi from "@/api/tripApi.js";
import memberApi from "@/api/memberApi.js";      
import merchantApi from "@/api/merchantApi.js";

export const useTravelCreateStore = defineStore('travelCreate', () => {
    const tripName = ref("")
    const startDate = ref()
    const endDate = ref()
    const budget = ref()

    return { tripName, startDate, endDate, budget }
});

export const usePaymentlistStore = defineStore('paymentlist', () => {
    const currentTrip = ref(null)
    const currentTripId = ref(null)
    const currentTripMembers = ref([]); // 현재 여행 멤버 목록
    const merchantCategories = ref([]);

    const setCurrentTripId = (tripId) => {
    currentTripId.value = tripId ?? null
    if (tripId) localStorage.setItem('currentTripId', String(tripId))
  }

    // 여행 목록 불러오기
    const fetchTrip = async (tripId) => {
    const id =
      tripId ??
      currentTripId.value ??
      Number(localStorage.getItem('currentTripId') || NaN)

    if (!id) throw new Error('fetchTrip: tripId가 필요합니다.')

    const detail = await tripApi.getTripDetail(id)
    currentTrip.value = detail
    setCurrentTripId(id)
    return detail
  }


    // 여행 상세 정보에서 member.userId 목록을 받아오고
    // getUserInfo()로 닉네임 붙이기
    const fetchCurrentTripMemberNicknames = async () => {
        if (!currentTrip.value) return;

        try{
            // 진행 중 여행 상세 정보 조회
            const detail = currentTrip.value;
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
    // state
    currentTripId,
    currentTrip,
    currentTripMembers,
    merchantCategories,
    // actions
    setCurrentTripId,
    fetchTrip,
    fetchCurrentTripMemberNicknames,
    fetchMerchantCategories,
    };
});