import {defineStore} from 'pinia'
import tripApi from "@/api/tripApi.js";
import memberApi from "@/api/memberApi.js";      // ✅ 추가
import merchantApi from "@/api/merchantApi.js";
import {ref} from 'vue';

export const useTravelCreateStore = defineStore('travelCreate', () => {
    const tripName = ref("")
    const startDate = ref()
    const endDate = ref()
    const budget = ref()

    return { tripName, startDate, endDate, budget }
});

export const usePaymentListStore = defineStore('paymentList', () => {
    const trip = ref(null);
    const currentTripMembers = ref([]); // 현재 여행 멤버 목록
    const merchantCategories = ref([]);

    const fetchTripDetail = async (tripId) => {
        trip.value = await tripApi.getTripDetail(tripId);
    };


    // 여행 상세 정보에서 member.userId 목록을 받아오고
    // getUserInfo()로 닉네임 붙이기
    const fetchCurrentTripMemberNicknames = async () => {
        if (!trip.value) return;

        try{
            // 진행 중 여행 상세 정보 조회
            const detail = await tripApi.getTripDetail(trip.value.tripId)
            const members = Array.isArray(detail.members) ? detail.members : []
            console.log("in store: "+members)

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
        trip,
        fetchTripDetail,
        currentTripMembers,
        fetchCurrentTripMemberNicknames,
        fetchMerchantCategories,
        merchantCategories
    };
});
