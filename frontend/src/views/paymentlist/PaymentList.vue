<template>
    <Header title="진행 중인 여행" />
    <div class="content-container">
      <!-- 현재 userId = 1인 여행만 보임 (TripController) -->
      <TravelCard
        v-if="tripStore.currentTrip"
        :trip-name="tripStore.currentTrip.tripName"
        :start-date="formatDate(tripStore.currentTrip.startDate)"
        :end-date="formatDate(tripStore.currentTrip.endDate)"
        v-model:activeTab="activeTab"
        showEdit
      />

      <div v-else>현재 진행 중인 여행이 없습니다.</div>     

      <Summary
        v-if="tripStore.currentTrip"
        :amount="totalAmount"
        :budget="tripStore.currentTrip.budget"
      >
      </Summary> 
      
      <Filter 
        v-if="tripStore.currentTrip"
        :start-date="formatDate(tripStore.currentTrip.startDate)"
        :members="tripStore.currentTripMembers" 
        @date-filtered="onDateFiltered" 
        @participant-filtered="onParticipantFiltered"
        @category-filtered="onCategoryFiltered"
      />  

      <PaymentListInfo 
        v-if="activeTab === '그룹 지출 내역' || activeTab === '선결제 내역'"
        :date-range="selectedDateRange" 
        :selected-participants="selectedParticipants"
        :selected-categories="selectedCategories"
        :active-tab="activeTab"
        @init-total="onInitTotal" 
      />
    </div>
      <!-- 고정 버튼 -->
    <button class="floating-add-button" @click="goToRegister">
      + 결제 추가
    </button>
</template>

<script setup>
import Header from '@/components/layout/Header.vue';
import TravelCard from '@/components/common/TravelCard.vue';
import Filter from '@/components/paymentlist/Filter.vue';
import PaymentListInfo from './PaymentListInfo.vue';

import { onMounted, ref } from 'vue';
import { useTripStore } from '@/stores/trip.js';
import { useRouter } from 'vue-router';
import Summary from '@/components/common/Summary.vue';

const router = useRouter();
const tripStore = useTripStore();
const activeTab = ref('그룹 지출 내역');

const goToRegister = () => {
  router.push('/paymentlist/register2');
};


const formatDate = (dateInput) => {
  let date;

  if (Array.isArray(dateInput) && dateInput.length === 3) {
    const [year, month, day] = dateInput;
    date = new Date(year, month - 1, day);
  } else if (typeof dateInput === 'string') {
    date = new Date(dateInput);
  } else {
    return '날짜 오류';
  }

  return date
    .toLocaleDateString('ko-KR', {
      year: 'numeric',
      month: '2-digit',
      day: '2-digit',
      timeZone: 'Asia/Seoul',
    })
    .replace(/\.\s/g, '.')
    .replace(/\.$/, '');
};


// Filter.vue에서 emit된 ref
const selectedDateRange = ref({ start: '', end: '' })
const selectedParticipants = ref([]); // 선택된 참여자 userId 배열
const selectedCategories = ref([])

const tripMembers = ref([]);
const totalAmount = ref(0)

// Filter.vue로부터 'date-filtered' 이벤트 전달받는 핸들러
function onDateFiltered(range) {
  console.log('[PaymentList.vue] 받은 날짜 범위:', range)
  // 부모 컴포넌트가 선택된 날짜 값을 보관
  selectedDateRange.value = range
}

// PaymentListInfo에서 전체 총액을 보내줄 때만 저장
function onInitTotal(amount){
  totalAmount.value = amount
}

function onParticipantFiltered(userIds){
  console.log('[Paymentlist.vue] 선택된 결제 참여자: ', userIds);
  selectedParticipants.value = userIds;
}

function onCategoryFiltered(categoryIds){
  console.log('[PaymentList.vue] 선택된 카테고리: ', categoryIds)
  selectedCategories.value = categoryIds
}

onMounted(async () => {
  await tripStore.fetchTrips()
  console.log("currenttrip: ",tripStore.currentTrip)
  if (tripStore.currentTrip) {
    await tripStore.fetchCurrentTripMemberNicknames()
  }
})
</script>

<style scoped>
.content-container {
  flex-grow: 1;
  padding: 1.25rem;
  overflow-y: auto;
  padding-top: 56px;
  position: relative;
  max-width: 384px;
  /* margin: 0 auto; */
}

/* 스크롤바 */
.content-container::-webkit-scrollbar {
  width: 8px;
}

.content-container::-webkit-scrollbar-track {
  background: #f0f0f0;
  border-radius: 50px;
}

.content-container::-webkit-scrollbar-thumb {
  background-color: #bbb;
  border-radius: 50px;
  border: 2px solid transparent;
  background-clip: padding-box;
}

.content-container::-webkit-scrollbar-thumb:hover {
  background-color: #888;
}

.floating-add-button {
  position: fixed;
  bottom: 90px;
  left: 55%;
  transform: translateX(-50%); /* 가운데 정렬 */
  width: 120px;
  max-width: 384px;

  background-color: rgb(255, 217, 130);
  color: #4A4A4A;
  font-weight: bold;
  font-size: 16px;
  padding: 14px 0;
  border: none;
  border-radius: 9999px;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
  cursor: pointer;
  z-index: 1000;
  transition: background-color 0.2s ease, transform 0.1s ease;
  font-family: 'IBM Plex Sans KR', sans-serif;
}

.floating-add-button:hover {
  background-color: #FFD166;
}

.floating-add-button:active {
  transform: translateX(-50%) scale(0.95);
}
</style>
