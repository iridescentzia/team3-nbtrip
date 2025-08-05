<script setup>
import { useRouter } from 'vue-router';

// 부모 컴포넌트(Home.vue)로부터 'settlements'라는 이름의 Array prop을 받습니다.
defineProps({
  settlements: {
    type: Array,
    required: true,
  },
});

const router = useRouter();

// '정산하기' 버튼을 눌렀을 때, 해당 여행의 개인화된 정산 페이지로 이동하는 함수
const goToSettlementDetail = (tripId) => {
  // 개인화된 정산 페이지 주소로 이동합니다.
  router.push(`/settlement/${tripId}/detail`);
};
</script>

<template>
  <!-- settlements 배열에 데이터가 있을 경우에만 목록을 표시합니다. -->
  <div v-if="settlements && settlements.length > 0" class="card-container">
    <!-- v-for를 사용해 배열의 각 항목에 대해 카드를 하나씩 생성합니다. -->
    <div
      v-for="settlement in settlements"
      :key="settlement.tripId"
      class="settlement-card"
    >
      <!-- 여행 정보 (이름, 날짜) -->
      <div>
        <p class="trip-name">{{ settlement.tripName }}</p>
        <p class="trip-dates">
          {{ settlement.startDate }} ~ {{ settlement.endDate }}
        </p>
      </div>

      <!-- 정산하기 버튼 -->
      <button
        @click="goToSettlementDetail(settlement.tripId)"
        class="settle-button"
      >
        <span>정산하기</span>
        <svg
          class="arrow-icon"
          xmlns="http://www.w3.org/2000/svg"
          fill="none"
          viewBox="0 0 24 24"
          stroke="currentColor"
        >
          <path
            stroke-linecap="round"
            stroke-linejoin="round"
            stroke-width="2.5"
            d="M9 5l7 7-7 7"
          />
        </svg>
      </button>
    </div>
  </div>
</template>

<style scoped>
/* 카드들을 감싸는 컨테이너 */
.card-container {
  display: flex;
  flex-direction: column;
  gap: 12px; /* 카드 사이의 간격 */
}

/* 개별 정산 카드 스타일 */
.settlement-card {
  width: 100%;
  background-color: #fff;
  border-radius: 12px;
  box-sizing: border-box;
  padding: 16px;
  box-shadow: 0 2px 6px rgba(0, 0, 0, 0.05);
  display: flex;
  justify-content: space-between;
  align-items: center;
  transition: transform 0.2s ease-in-out, box-shadow 0.2s ease-in-out;
  cursor: pointer;
}

.settlement-card:hover {
  transform: translateY(-2px); /* 호버 시 살짝 떠오르는 효과 */
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
}

/* 여행 이름 스타일 */
.trip-name {
  font-weight: 700;
  color: #4a4a4a; /* --theme-text */
  margin: 0;
}

/* 여행 날짜 스타일 */
.trip-dates {
  font-size: 14px;
  color: #aaaaaa; /* --theme-text-light */
  margin-top: 4px;
  margin: 0;
}

/* 정산하기 버튼 스타일 */
.settle-button {
  display: flex;
  align-items: center;
  font-weight: 700;
  color: #4a4a4a; /* --theme-primary */
  background: none;
  border: none;
  cursor: pointer;
  font-size: 14px;
  padding: 2px;
  border-radius: 8px;
  transition: background-color 0.2s;
}

.settle-button:hover {
  background-color: #f8fafc; /* --theme-bg-alt */
}

.arrow-icon {
  width: 16px;
  height: 16px;
  margin-left: 4px;
}
</style>
