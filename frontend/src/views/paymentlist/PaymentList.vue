<template>
  <!-- <DefaultLayout> -->
    <Header title="진행 중인 여행" />
    <div class="content-container">
      <!-- 현재 tripId = 1 -->
      <TravelCard
        v-if="tripStore.currentTrip"
        :trip-name="tripStore.currentTrip.tripName"
        :start-date="formatDate(tripStore.currentTrip.startDate)"
        :end-date="formatDate(tripStore.currentTrip.endDate)"
        showEdit
      />

      <div v-else>현재 진행 중인 여행이 없습니다.</div>
      <!-- <Summary :amount="1000" :budget="20000"></Summary> -->

      <Summary
        v-if="tripStore.currentTrip"
        :amount="1000000"
        :budget="tripStore.currentTrip.budget"
      >
      </Summary>
    
      <Filter 
        v-if="tripStore.currentTrip"
        :start-date="formatDate(tripStore.currentTrip.startDate)"
        @date-filtered="onDateFiltered" 
      />  

      <PaymentListInfo :date-range="selectedDateRange" />
    </div>
  <!-- </DefaultLayout> -->
</template>

<script setup>
import Header from '@/components/layout/Header.vue';
import TravelCard from '@/components/common/TravelCard.vue';
import DefaultLayout from '@/components/layout/DefaultLayout.vue';
import Filter from '@/components/paymentlist/Filter.vue';
import PaymentListInfo from './PaymentListInfo.vue';

import { onMounted, ref } from 'vue';
import { useTripStore } from '@/stores/trip.js';
import Summary from '@/components/common/Summary.vue';

const tripStore = useTripStore();

// timestamp -> KST 기준 date 변환
// const formatDate = (timestamp) => {
//   const date = new Date(timestamp);
//   return date
//     .toLocaleDateString('ko-KR', {
//       year: 'numeric',
//       month: '2-digit',
//       day: '2-digit',
//       timeZone: 'Asia/Seoul',
//     })
//     .replace(/\.\s/g, '.')
//     .replace(/\.$/, ''); // 2025.07.30 형태로 맞춤
// };

const formatDate = (dateArray) => {
  if (!Array.isArray(dateArray) || dateArray.length !== 3) return '날짜 오류';

  const [year, month, day] = dateArray;
  const date = new Date(year, month - 1, day); // ← ⚠ month - 1 필수

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

// Filter.vue에서 emit된 날짜 범위를 저장할 ref
const selectedDateRange = ref({ start: '', end: '' })

// Filter.vue로부터 'date-filtered' 이벤트 전달받는 핸들러
function onDateFiltered(range) {
  console.log('[PaymentList.vue] 받은 날짜 범위:', range)
  // 부모 컴포넌트가 선택된 날짜 값을 보관
  selectedDateRange.value = range
}

// watch(
//   () => tripStore.currentTrip,
//   (newTrip) => {
//     console.log('[watch] currentTrip 변경됨:', newTrip);
//   },
//   { immediate: true }
// );

onMounted(async () => {
  await tripStore.fetchTrips();
  console.log("디버깅 trips:", tripStore.trips);

    tripStore.trips.forEach((trip, index) => {
    console.log(`Trip[${index}]`, trip);
    
  });

  // const now = Date.now();
  // tripStore.trips.forEach((trip)=>{
  //   console.log(
  //     `[tripId: ${trip.tripId}] status: ${trip.tripStatus}, start: ${trip.startDate}, end: ${trip.endDate}, now: ${now}`
  //   );
  // })

  await tripStore.fetchCurrentTripMembers()
  console.log('currentTrip:', tripStore.currentTrip);
  console.log("currentTrip.value: ", tripStore.currentTrip);
});
</script>

<style scoped>
.content-container {
  flex-grow: 1;
  padding: 1.25rem;
  overflow-y: auto;
  padding-top: 56px;
}
</style>
