<script setup>
import { onMounted } from 'vue';
import { storeToRefs } from 'pinia';
import Header from '@/components/layout/Header2.vue';
import Button from '@/components/common/Button.vue';
import { useRoute, useRouter } from 'vue-router';
import catImage from '@/assets/img/crying_cat.png';
import { useSettlementStore } from '@/stores/settlementStore';

// ✅ Pinia Store 사용
const settlementStore = useSettlementStore();
const { mySettlementData, isLoading, error } = storeToRefs(settlementStore);

const router = useRouter();
const route = useRoute();
const tripId = route.params.tripId;

// 미정산 내역으로 이동 (재시도)
const goBack = () => {
  router.push(`/settlement/${tripId}/detail`);
};

// 데이터 로딩
onMounted(async () => {
  try {
    await settlementStore.fetchMySettlement(tripId);
  } catch (err) {
    // 에러 처리는 store에서 담당
  }
});
</script>

<template>
  <div class="settlement-view">
    <Header title="정산하기" />

    <main v-if="isLoading" class="content-container loading">
      <p>여행 정보를 불러오는 중...</p>
    </main>

    <main v-else-if="error" class="content-container error">
      <p>{{ error }}</p>
    </main>

    <main v-else class="content-container">
      <div class="content">
        <img class="cat-img" :src="catImage" alt="우는 고양이" />
        <div class="text-area">
          <p class="trip-name">{{ mySettlementData?.tripName || '여행' }}</p>
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
</template>

<style scoped>
/* 전체 레이아웃 */
.settlement-view {
  width: 100%;
  height: 100%;
  background-color: var(--theme-bg);
  display: flex;
  flex-direction: column;
  position: relative; /* Header의 absolute 포지션 기준점 */
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
