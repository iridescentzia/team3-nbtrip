<!--임시 코드임! 정식 아님-->
<script setup>
import Filter from '@/components/paymentlist/Filter.vue';
import PaymentListInfo from "@/views/paymentlist/PaymentListInfo.vue";
import tripApi from "@/api/tripApi.js";
import {useRouter, useRoute} from "vue-router";
import { onMounted, ref } from 'vue';
import { useTripStore } from '@/stores/trip.js';
import Summary from '@/components/common/Summary.vue';
const route = useRoute();
const router = useRouter();
const tripStore = useTripStore();
const trip = ref();

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

function handleTripEnd(){
  tripApi.closeTrip(route.params.tripId)
  router.push(`/settlement/${route.params.tripId}`)
  alert("여행이 종료되었습니다.")

}

onMounted(async () => {
  await tripStore.fetchTrips()
  trip.value= await tripApi.getTripDetail(route.params.tripId);
  if (tripStore.currentTrip) {
    await tripStore.fetchCurrentTripMemberNicknames()
  }
})
</script>

<!--테스트를 위한 임시 코드. -->
<template>
  <div>
    <Summary
        v-if="trip"
        :amount="totalAmount"
        :budget="trip.budget"
        :onTerminate="handleTripEnd"
    >
    </Summary>

    <Filter
        v-if="trip"
        :start-date="formatDate(trip.startDate)"
        :members="tripStore.currentTripMembers"
        @date-filtered="onDateFiltered"
        @participant-filtered="onParticipantFiltered"
        @category-filtered="onCategoryFiltered"
    />

    <PaymentListInfo
        :date-range="selectedDateRange"
        :selected-participants="selectedParticipants"
        :selected-categories="selectedCategories"
        @init-total="onInitTotal"
    />
  </div>
</template>

<style scoped>
div {
  overflow-y: auto;
}

/* 스크롤바 */
div::-webkit-scrollbar {
  width: 8px;
}

div::-webkit-scrollbar-track {
  background: #f0f0f0;
  border-radius: 50px;
}

div::-webkit-scrollbar-thumb {
  background-color: #bbb;
  border-radius: 50px;
  border: 2px solid transparent;
  background-clip: padding-box;
}

div::-webkit-scrollbar-thumb:hover {
  background-color: #888;
}

</style>

