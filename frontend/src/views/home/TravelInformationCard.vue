<script setup>
import { ref, onMounted, computed } from 'vue';
import tripApi from '@/api/tripApi.js';
import paymentlistApi from '@/api/paymentlistApi.js'


// ✅ props.tripId 받아오기
const props = defineProps({
  tripId: Number
});

const trip = ref(null);
const paymentList = ref([]);

// tripName
const tripName = computed(() => trip.value?.tripName || '');


// 날짜 포맷 (2025.08.01 - 2025.08.04)
const date = computed(() => {
  if (!trip.value) return '';
  const { startDate, endDate } = trip.value;
  return `${startDate} - ${endDate}`;
});

// 사용 금액
const usedAmount = computed(() => {
  return paymentList.value.reduce((sum, item) => sum + item.amount, 0);
});

// 예산
const budget = computed(() => trip.value?.budget || 0);

// 포맷팅
const formattedAmount = computed(() => usedAmount.value.toLocaleString() + '원');
const formattedBudget = computed(() => budget.value.toLocaleString() + '원');

// 진행률
const progressPercentage = computed(() => {
  if (!budget.value) return 0;
  return Math.min((usedAmount.value / budget.value) * 100, 100).toFixed(1);
});

const isOverBudget = computed(() => usedAmount.value >= budget.value);

onMounted(async () => {
  const detail = await tripApi.getTripDetail(props.tripId);
  console.log('tripApi 응답 detail:', detail);
  trip.value = detail;

  const res = await paymentlistApi.getPaymentList(props.tripId);
  console.log('🔥 res:', res);

  if (res?.paymentData && Array.isArray(res.paymentData)) {
    paymentList.value = res.paymentData;
    console.log(`[tripId=${props.tripId}] 결제 내역:`, paymentList.value);
  } else {
    console.warn('❌ 결제 데이터 형식이 예상과 다름:', res);
  }
});

</script>

<template>
  <div class="travel-card">
    <h3 class="trip-name">{{ tripName }}</h3>
    <p class="trip-date">{{ date }}</p>

    
    <div class="amount-row">
        <span class="amount-text">현재 사용 금액</span>
        <div class="amount"> 
            {{formattedAmount}}
        </div>
    </div>

    <div class="progress-bar">
      <div
          class="progress"
          :class="{over: isOverBudget}"
          :style="{width: progressPercentage + '%'}"
      ></div>
    </div>
  </div>
</template>



<style scoped>
.travel-card {
  width: 100%;
  background-color: #fff;
  border-radius: 12px;
  box-sizing: border-box;
  padding: 16px;
  box-shadow: 0 2px 6px rgba(0,0,0,0.05);
  display: flex;
  flex-direction: column;
  
}

.trip-name {
  font-size: 20px;
  color: black;
  margin: 0%;
}

.trip-date {
  font-size: 15px;
  color: #666;
  margin: 0%;
  
}

.amount-row {
  display: flex;
  flex-direction: column;
  align-items: flex-start;
  margin-top: 15%;
}

.amount-text{
    font-size: 15px;
    color: #666;
}
.amount{
    font-size: 30px;     /* 💡 폰트 크기 키움 */
    font-weight: bold;   /* 💡 볼드체로 */
}

.progress-bar {
  width: 100%;
  height: 8px;
  background-color: #eee;
  border-radius: 4px;
  overflow: hidden;
}

.progress {
  height: 100%;
  background-color: #5C8EF6;
  transition: width 0.3s ease;
}

.progress.over {
  background-color: #ff6666;
}

</style>
