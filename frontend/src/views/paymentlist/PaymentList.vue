<template>
  <DefaultLayout>
<!--    <Header title="진행 중인 여행" />-->
    <section class="trip-detail">
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
<!--          :trip-name="tripStore.trip.groupName"-->
<!--          :start-date="tripStore.trip.startDate"-->
<!--          :end-date="tripStore.trip.endDate"-->
<!--      />-->
      <Summary :amount="1000000" :budget="2000000"></Summary>

<!--        pinia ver.  -->
<!--  <Summary budget="tripStore.trip?.budget || 0" amount="totalAmount"></Summary>-->

      <!-- 필터 -->
      <div class="filters">
        <select><option>날짜</option></select>
        <select><option>카테고리</option></select>
        <select><option>결제 참여자</option></select>
      </div>

      <!-- 지출 내역 -->
<!--      <div class="expense-list">-->
<!--        <div class="expense-item">-->
<!--          <div class="title">서울역-부산역 KTX (4인)</div>-->
<!--          <div class="meta">이건우 · 오전 09:30</div>-->
<!--          <div class="price">150,000원</div>-->
<!--        </div>-->
<!--        <div class="expense-item">-->
<!--          <div class="title">홍대 에어비앤비 (2박)</div>-->
<!--          <div class="meta">김민수 · 오전 09:00</div>-->
<!--          <div class="price">150,000원</div>-->
<!--        </div>-->
<!--      </div>-->
    </section>

    <!-- 하단 버튼 -->
<!--    <div class="floating-button">-->
<!--      <Button>+ 기타 결제</Button>-->
<!--    </div>-->
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
.terminate{
  border:none;
}
.floating-button {
  position: fixed;
  bottom: 80px; /* 푸터 위에 여유 있게 */
  left: 16px;
  z-index: 1001;

  padding: 6px 16px;
  font-size: 13px;
  font-weight: 600;

  background-color: #facc15; /* 노란색 */
  color: #374151; /* 텍스트 색상 */
  border: none;
  border-radius: 9999px; /* 알약 형태 */
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.1);
  cursor: pointer;
  transition: background-color 0.2s ease, transform 0.1s ease;
  line-height: 1; /* +가 중앙 정렬 안 될 경우 대비 */
}

.floating-button:hover {
  background-color: #eab308; /* hover용 더 진한 노랑 */
}

.floating-button:active {
  transform: scale(0.96);
}

.trip-detail {
  padding: 16px;
  padding-bottom: 120px;
  box-sizing: border-box;
  //padding-top: 56px;
}

.tab-group {
  display: flex;
  gap: 8px;
  margin-top: 12px;
}

.tab {
  flex: 1;
  padding: 8px 12px;
  font-size: 14px;
  font-weight: 500;
  border-radius: 8px;
  background-color: #f5f5f5;
  color: #555;
  border: none;
  cursor: pointer;
}

.tab.active {
  background-color: #e0f0ff;
  color: #2f6be9;
}

.summary-card {
  margin-top: 16px;
  background: #ffffff;
  border-radius: 12px;
  box-shadow: 0 2px 6px rgba(0, 0, 0, 0.05);
  padding: 16px;
}

.summary-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
}

.label {
  font-size: 13px;
  color: #888;
}

.amount {
  font-size: 28px;
  font-weight: bold;
  margin-bottom: 8px;
}

.progress-bar {
  height: 8px;
  background: #eee;
  border-radius: 4px;
}

.progress {
  width: 70%; /* 예시 비율 */
  height: 100%;
  background-color: #2f6be9;
  border-radius: 4px;
}

.filters {
  display: flex;
  gap: 8px;
  margin-top: 16px;
}

.filters select {
  flex: 1;
  padding: 8px;
  font-size: 14px;
  border-radius: 6px;
  border: 1px solid #ddd;
  color: #555;
}

.expense-list {
  margin-top: 16px;
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.expense-item {
  background-color: #fff;
  border-radius: 12px;
  padding: 12px;
  box-shadow: 0 2px 6px rgba(0, 0, 0, 0.05);
}

.expense-item .title {
  font-weight: 600;
  font-size: 15px;
  margin-bottom: 4px;
}

.expense-item .meta {
  font-size: 13px;
  color: #999;
}

.expense-item .price {
  text-align: right;
  font-weight: bold;
  font-size: 16px;
  margin-top: 4px;
}



</style>
