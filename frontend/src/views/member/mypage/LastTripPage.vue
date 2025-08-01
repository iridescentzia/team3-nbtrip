<script setup>
import { ref } from 'vue'
import TravelListCard from '@/components/mypage/TravelListCard.vue'

// 샘플 여행 데이터
const trips = ref([
  {
    tripId: 1,
    tripName: '서울 우정 여행',
    startDate: '2025-07-10',
    endDate: '2025-07-12'
  },
  {
    tripId: 2,
    tripName: '부산 바다 여행',
    startDate: '2025-06-15',
    endDate: '2025-06-17'
  },
  {
    tripId: 3,
    tripName: '제주도 힐링 여행',
    startDate: '2025-05-20',
    endDate: '2025-05-23'
  }
])

// 여행 상세 페이지로 이동하는 함수
const goToTripDetail = (tripData) => {
  console.log('클릭된 여행:', tripData)
  // 여기에 라우터 이동 로직을 추가하세요
  // 예: router.push(`/trip/${tripData.tripId}`)
}
</script>

<template>
  <div class="mypage-wrapper">
    <div class="content">
      <!-- 상단 제목 -->
      <header class="header"><h1>지난 여행</h1></header>

      <!-- 메뉴 리스트 -->
      <div class="menu-list">
        <TravelListCard
            v-for="trip in trips"
            :key="trip.tripId"
            :trip-name="trip.tripName"
            :start-date="trip.startDate"
            :end-date="trip.endDate"
            :trip-id="trip.tripId"
            @click="goToTripDetail"
        />
      </div>

    </div>

  </div>
</template>

<style scoped>
.mypage-wrapper {
  width: 384px;
  height: 800px;
  position: relative;
  background: #f8fafc;
  box-shadow: 0px 25px 50px -12px rgba(0, 0, 0, 0.25);
  overflow: auto;
  border-radius: 24px;
  outline: 1px black solid;
  outline-offset: -1px;
  margin: 0 auto;
  padding: 32px 32px 0;
  box-sizing: border-box;
  display: flex;
  flex-direction: column;
}

.content {
  flex: 1;
  display: flex;
  flex-direction: column;
  min-height: 0;
}

.header {
  text-align: center;
  font-size: 20px;
  font-weight: bold;
  color: #333;
  margin-bottom: 16px;
}

.header h1 {
  font-size: 20px;
  margin-bottom: 16px;
  font-weight: bold;
  color: #333;
}

.menu-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
  align-items: center;
}
</style>