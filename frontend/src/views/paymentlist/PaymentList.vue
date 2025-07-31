<template>
  <DefaultLayout>
    <Header title="진행 중인 여행" />
    <div class="content-container">

      <!-- 현재 userID = 1 -->
    <TravelCard
      v-if="tripStore.currentTrip"
      :trip-name="tripStore.currentTrip.tripName"
      :start-date="formatDate(tripStore.currentTrip.startDate)"
      :end-date="formatDate(tripStore.currentTrip.endDate)"
      showEdit
    />
    
    <div v-else>
      현재 진행 중인 여행이 없습니다.
    </div>
    
    <Summary 
      v-if="tripStore.currentTrip"
      :amount="1000000"  
      :budget="tripStore.currentTrip.budget">
    </Summary>

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
import Filter from '@/components/paymentlist/Filter.vue'

import {onMounted} from 'vue'
import {useTripStore} from "@/stores/trip2.js";
import Summary from "@/components/common/Summary.vue";

const tripStore = useTripStore()

// timestamp -> date 변환
const formatDate = (timestamp) => {
  const date = new Date(timestamp)
  return date.toISOString().split('T')[0].replace(/-/g, '.')
}

onMounted(async () => {
  await tripStore.fetchTrips()
  console.log('currentTrip:', tripStore.currentTrip)
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
