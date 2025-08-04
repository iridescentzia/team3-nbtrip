
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
  </div>
  <Footer />
</template>

<style scoped>

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