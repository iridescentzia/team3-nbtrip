<script setup>
import {ref, onMounted} from "vue";
import {useRouter} from "vue-router";
import tripApi from "@/api/tripApi.js";
import TravelListCard from "@/components/mypage/TravelListCard.vue";
import Header from "@/components/layout/Header.vue";

const trips = ref([])
const router = useRouter()

// 여행 상세 페이지 이동
const goToTripDetail = (tripId) => {
  router.push(`/paymentlist/${tripId}`)
}

// Header에서 이전 페이지로 이동
const goBack = () => {
  router.back();
};

// 지난 여행 목록 불러오기(TripStatus == 'CLOSED')
onMounted(async () => {
  try {
    const response = await tripApi.getTripsByStatus('CLOSED');
    console.log('지난 여행 응답:', response);
    trips.value = response || [];
  } catch (err) {
    console.log('지난 여행 조회 실패: ', err)
  }
})
</script>

<template>
  <div class="mypage-wrapper">
    <div class="content">
      <!-- 상단 제목 -->
      <Header class="header" title="지난 여행" :back-action="goBack"/>

      <!-- 메뉴 리스트 -->
      <div class="menu-list">
        <TravelListCard
            v-for="trip in trips"
            :key="trip.tripId"
            :trip-name="trip.tripName"
            :start-date="trip.startDate"
            :end-date="trip.endDate"
            :trip-id="trip.tripId"
            @click="goToTripDetail(trip.tripId)"
        />
      </div>
    </div>
  </div>
</template>

<style scoped>
.mypage-wrapper {
  width: 100%;
  height: 100%;
  background: #f8fafc;
  display: flex;
  flex-direction: column;
  align-items: center;
  overflow: hidden;
  padding-top: 0;
}


.content {
  width: 100%;
  max-width: 384px;
  flex: 1;
  padding: 56px 20px 0 20px;
  box-sizing: border-box;
  overflow-y: auto;
}

.menu-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
  margin-top: 20px;
}
</style>