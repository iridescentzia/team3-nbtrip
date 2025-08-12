<script setup>
import { ref, onMounted, computed } from 'vue';
import tripApi from '@/api/tripApi.js';
import paymentlistApi from '@/api/paymentlistApi.js'
import { ChevronRight } from 'lucide-vue-next';
import { useRouter } from 'vue-router';

const router = useRouter();
const goTripDetail = () => {
  if (props.empty || !trip.value) return;
  router.push(`/trip/${trip.value.tripId}`);
}

const props = defineProps({
  trip: { type: Object, default: null },
  empty: { type: Boolean, default: false }
});

const trip = ref(props.trip);
const paymentList = ref([]);

// tripName
const tripName = computed(() => trip.value?.tripName || '');


// 날짜 포맷 (2025.08.01 ~ 2025.08.04)
const toDot = (s) => String(s).replaceAll('-', '.');
const date = computed(() => {
  if (!trip.value) return '';
  const { startDate, endDate } = trip.value;
  return `${toDot(startDate)} ~ ${toDot(endDate)}`;
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
  if (props.empty || !trip.value) return;
  const res = await paymentlistApi.getPaymentList(trip.value.tripId);
  console.log('res:', res);

  if (res?.paymentData && Array.isArray(res.paymentData)) {
    paymentList.value = res.paymentData;
    console.log(`[tripId=${trip.value.tripId}] 결제 내역:`, paymentList.value);
  } else {
    console.warn('❌ 결제 데이터 형식이 예상과 다름:', res);
  }
});

</script>

<template>
  <div class="travel-card" :class="{ empty }" @click="goTripDetail">
    <div class="trip-header">
      <div>
        <h3 class="trip-name">
          <template v-if="!empty">{{ tripName }}</template>
          <template v-else>진행 중인 여행이 없어요!</template>
        </h3>
        <p v-if="!empty" class="trip-date">{{ date }}</p>
      </div>
      <ChevronRight v-if="!empty" class="arrow-icon" />
    </div>

    
    <div v-if="!empty" class="amount-row">
        <span class="amount-text">현재 사용 금액</span>
        <div class="amount"> 
          {{formattedAmount}}
        </div>
        
    </div>

    <div v-if="!empty" class="progress-bar">
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
  box-shadow: 0 2px 6px rgba(0,0,0,0.1);
  display: flex;
  flex-direction: column;
  font-family: 'IBM Plex Sans KR', sans-serif;
  cursor: pointer;
  transition: background-color 0.2s ease;
}

.travel-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
}

.travel-card.empty {
  cursor: default;
}
.travel-card.empty .arrow-icon {
  opacity: 0.4;
}
.travel-card.empty .trip-name {
  color: #6b7280; /* 회색 톤 */
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
  margin-top: 10%;
}

.amount-text{
    font-size: 15px;
    color: #666;
}
.amount{
    font-size: 30px;
    font-weight: bold;

    display: flex;
    align-items: center;
    gap: 30px; 
}

.trip-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  cursor: pointer;
}

.arrow-icon {
  width: 24px;
  height: 24px;
  color: #888;
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
