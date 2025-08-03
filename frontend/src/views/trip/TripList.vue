
<script setup>
import Header from "@/components/layout/Header.vue";
import Footer from "@/components/layout/Footer.vue";
import TravelListCard from "@/components/mypage/TravelListCard.vue";
import tripApi from "@/api/tripApi.js";
import {ref} from "vue";
const joinedTripList = ref([]);
import {useRouter} from "vue-router";

const router = useRouter();
const handleCardClick = (trip) => {
  console.log('클릭한 여행: ', trip);
  router.push(`/trip/${trip.tripId}`);
};

const load = async () => {
  joinedTripList.value = await tripApi.fetchTrips();
  console.log(joinedTripList.value);
};
load();


</script>

<template>
  <div class="view-wrapper">
    <div class="trip-detail-view">
      <Header title="전체 여행 목록" />
      <div class="content-container">
        <TravelListCard
            v-for="trip in joinedTripList"
            :key="trip.tripId"
            :trip-name="trip.tripName"
            :start-date="trip.startDate"
            :end-date="trip.endDate"
            :trip-id="trip.tripId"
            @click="handleCardClick"
        />
        <br>
      </div>
      <Footer />
    </div>
  </div>
</template>

<style scoped>
.trip-detail-view {
  --theme-primary: rgba(255, 209, 102, 0.65);
  --theme-primary-dark: #e2c05e;
  --theme-bg: #f8f9fa;
  --theme-text: #333333;
  --theme-text-light: #888888;
}

/* 화면 중앙 정렬을 위한 wrapper 스타일 */
.view-wrapper {
  display: flex;
  justify-content: center;
  width: 100%;
  min-height: 100vh; /* 화면 전체 높이를 차지하도록 */
  background-color: #ffffff;
  padding: 2rem 0; /* 위아래 여백 추가 */
}

/* 전체 레이아웃 */
.trip-detail-view {
  z-index: 1;
  width: 100%;
  max-width: 24rem; /* 384px */
  background-color: var(--theme-bg);
  display: flex;
  flex-direction: column;
  border-radius: 1.5rem; /* 둥근 모서리 */
  box-shadow: 0 25px 50px -12px rgb(0 0 0 / 0.25); /* 그림자 효과 */
  overflow: hidden; /* 둥근 모서리 적용을 위해 */
  position: relative; /* Header 컴포넌트의 fixed 포지션 기준점 */
  height: 844px; /* 특정 스마트폰 높이를 기준으로 고정 */
  max-height: 90vh; /* 화면 높이의 90%를 넘지 않도록 설정 */
}

/* 메인 콘텐츠 */
.content-container {
  flex-grow: 1;
  display: flex;
  flex-direction: column;
  gap: 15px;
  overflow-y: auto;
  padding: calc(56px) 1.25rem 1.25rem;
}

</style>