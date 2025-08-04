<script setup>
import Header from "@/components/layout/Header2.vue";
import Button from "@/components/common/Button.vue";
import { useRoute, useRouter } from "vue-router";
import catImage from "@/assets/img/smiling_cat.png";
import axios from "axios";
import { ref, onMounted } from "vue";

const router = useRouter();
const route = useRoute();
const tripId = route.params.tripId;

const trip = ref({ tripName: "" });

const goToTravelReport = () => {
  router.push(`/report/${tripId}`);
};

const fetchTrip = async () => {
  try {
    const { data } = await axios.get(`/api/trips/${tripId}`);
    trip.value = data;
  } catch (error) {
    console.error("여행 정보 불러오기 실패: ", error);
  }
};

onMounted(fetchTrip);
</script>

<template>
  <div class="settlement-complete-view">
    <Header title="정산하기" />

    <main class="content-container">
      <div class="content">
        <img class="cat-img" :src="catImage" alt="웃고 있는 고양이" />
        <div class="text-area">
          <p class="trip-name">{{ trip.tripName }}</p>
          <h2 class="main-msg">정산이 완료되었습니다.</h2>
        </div>
      </div>
    </main>

    <footer class="footer">
      <Button
          label="여행 리포트 보러 가기"
          @click="goToTravelReport"
          class="report-button"
      />
    </footer>
  </div>
</template>

<style scoped>
.settlement-complete-view {
  width: 100%;
  height: 100vh;
  background-color: var(--theme-bg);
  display: flex;
  flex-direction: column;
  position: relative;
}

.content-container {
  flex-grow: 1;
  display: flex;
  flex-direction: column;
  justify-content: center;
  padding: 1.25rem;
  padding-top: calc(56px + 1.25rem); /* 헤더 높이 고려 */
}

.content {
  display: flex;
  flex-direction: column;
  align-items: center;
  text-align: center;
}

.cat-img {
  width: 180px;
  margin-bottom: 24px;
}

.text-area {
  text-align: center;
}

.trip-name {
  color: var(--theme-text-light);
  font-size: 0.875rem;
  margin-bottom: 6px;
}

.main-msg {
  font-size: 1.25rem;
  font-weight: bold;
  color: var(--theme-text);
  margin-bottom: 6px;
}

.footer {
  padding: 1rem;
  background-color: var(--theme-bg);
  margin-top: auto;
}

.report-button {
  width: 100%;
  font-weight: 800;
  padding: 1rem 0;
  border-radius: 0.75rem;
  border: none;
  cursor: pointer;
}
</style>