<template>
  <DefaultLayout>
    <Header title="진행 중인 여행" />
    <div class="content-container">
<!--       여행 정보 카드 -->
<!--      DefaultLayout.vue에서 pinia 처리함 (보여주기식 하드코딩)-->
      <TravelCard
          :trip-name="'서울 우정 여행'"
          :start-date="'2025.07.10'"
          :end-date="'2025.07.12'"
          showEdit
      />

<!--      <TravelCard-->
<!--          v-if="tripStore.trip"-->
<!--          :trip-name="tripStore.trip.tripName"-->
<!--          :start-date="tripStore.trip.startDate"-->
<!--          :end-date="tripStore.trip.endDate"-->
<!--      />-->
      <Summary :amount="1000000" :budget="2000000"></Summary>

<!--        pinia ver.  -->
<!--  <Summary budget="tripStore.trip?.budget || 0" amount="totalAmount"></Summary>-->

      <!-- 필터 -->
      <Filter></Filter>
    </div>
  </DefaultLayout>
</template>

<script setup>
import Header from '@/components/layout/Header.vue'
import TravelCard from '@/components/common/TravelCard.vue'
import Button from '@/components/common/Button.vue'
import DefaultLayout from '@/components/layout/DefaultLayout.vue'

import {onMounted} from 'vue'
import {useTripStore} from "@/stores/trip.js";
import Summary from "@/components/common/Summary.vue";

const tripStore = useTripStore()

// 컴포넌트가 마운트될 때 trip 정보 가져오기
onMounted(()=>{
  tripStore.fetchActiveTrip()
})
</script>

<style scoped>
.content-container{
  flex-grow:1;
  padding: 1.25rem;
  overflow-y: auto;
  padding-top: 56px;
}
</style>
