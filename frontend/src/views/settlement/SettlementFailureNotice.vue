<script setup>
import Header from "@/components/layout/Header2.vue";
import Button from "@/components/common/Button.vue";
import { useRoute, useRouter } from "vue-router";
import tripApi from "@/api/tripApi.js";
import catImage from "@/assets/img/crying_cat.png";
import { onMounted, ref } from "vue";
import {getMySettlementDetails} from "@/api/settlementApi.js";

const router = useRouter()
const route = useRoute()
const tripId = route.params.tripId

const settlementData = ref(null)
const isLoading = ref(true)
const error = ref(null)

// 미정산 내역으로 이동
const goBack = () => {
  router.push(`/settlement/${tripId}/detail`)
}

const fetchTrip = async () => {
  try {
    const response = await getMySettlementDetails(tripId);
    settlementData.value = response.data;
  } catch (err) {
    console.error('개인 정산 정보 로딩 실패:', err);
    error.value = '데이터를 불러오는 데 실패했습니다.';
  } finally {
    isLoading.value = false;
  }
}

onMounted(() => {
  fetchTrip()
})
</script>

<template>
  <!-- ✅ DetailView와 동일한 wrapper 구조 -->
  <div class="view-wrapper">
    <div class="settlement-view">
      <Header title="정산하기" />

      <!-- ✅ 로딩 상태 -->
      <main v-if="isLoading" class="content-container loading">
        <p>여행 정보를 불러오는 중...</p>
      </main>

      <!-- ✅ 에러 상태 -->
      <main v-else-if="error" class="content-container error">
        <p>{{ error }}</p>
      </main>

      <!-- ✅ 정상 데이터 -->
      <main v-else class="content-container">
        <div class="content">
          <img class="cat-img" :src="catImage" alt="우는 고양이"/>
          <div class="text-area">
            <p class="trip-name">{{ settlementData.tripName }}</p>
            <h2 class="main-msg">송금이 일부 실패했습니다.</h2>
          </div>
        </div>
      </main>

      <footer class="footer">
        <Button
            label="미정산 내역으로 돌아가기"
            @click="goBack"
            class="back-button"
        />
      </footer>
    </div>
  </div>
</template>

<style scoped>
.settlement-view {
  --theme-primary: rgba(255, 209, 102, 0.65);
  --theme-secondary: rgba(162, 210, 255, 0.65);
  --theme-bg: #f8f9fa;
  --theme-text: #333333;
  --theme-text-light: #888888;
  --theme-red: #ef4444;
  --theme-blue: #3a86ff;
}

.view-wrapper {
  display: flex;
  justify-content: center;
  width: 100%;
  min-height: 100vh;
  background-color: #ffffff;
  padding: 2rem 0;
}

.settlement-view {
  z-index: 1;
  width: 100%;
  max-width: 24rem; /* 384px */
  background-color: var(--theme-bg);
  display: flex;
  flex-direction: column;
  border-radius: 1.5rem;
  box-shadow: 0 25px 50px -12px rgb(0 0 0 / 0.25);
  overflow: hidden;
  position: relative;
  height: 844px;
  max-height: 90vh;
}

.content-container {
  flex-grow: 1;
  padding: 1.25rem;
  overflow-y: auto;
  padding-top: calc(56px + 1.25rem);
}

.loading,
.error {
  display: flex;
  align-items: center;
  justify-content: center;
  text-align: center;
  color: var(--theme-text-light);
  padding-top: 56px;
  flex-grow: 1;
}

.content {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 100%;
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
  font-size: 0.875rem; /* 14px */
  margin-bottom: 6px;
}

.main-msg {
  font-size: 1.25rem; /* 20px */
  font-weight: 800;
  color: var(--theme-text);
  margin-bottom: 6px;
}

.sub-msg {
  font-size: 0.875rem; /* 14px */
  color: var(--theme-text-light);
}

.footer {
  padding: 1rem;
  background-color: var(--theme-bg);
  margin-top: auto;
  border-top: none;
}

.back-button {
  width: 100%;
  font-weight: 800 !important;
  padding: 1rem 0 !important;
  border-radius: 0.75rem !important;
  transition: opacity 0.2s, background-color 0.2s !important;
  border: none !important;
  cursor: pointer !important;
}

.back-button:hover {
  opacity: 0.9 !important;
}
</style>