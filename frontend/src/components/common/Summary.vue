<template>
  <div class="summary-card">
    <div class="summary-header">
      <span class="label">총 사용 금액</span>
      <button class="terminate" small>여행 종료하기</button>
    </div>
    <div class="amount-row">
      <div class="amount">{{formattedAmount}}</div>
      <Info class="info-icon" />
<!--      <div class="budget" small >예산: {{formattedBudget}}</div>-->
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

<script setup >
import {computed} from 'vue'
import { Info } from 'lucide-vue-next';

// props: amount(사용금액 합계), budget(여행 예산)
const props = defineProps({
  amount: {
    type: Number,
    required: true
  },
  budget:{
    type: Number,
    required: true
  }
})

// 총 금액 포맷팅 (ex. 350,000원)
const formattedAmount = computed(()=>{
  return props.amount.toLocaleString() + '원'
})

// 예산 포맷팅 (ex. 350,000원)
const formattedBudget = computed(()=>{
  return props.budget.toLocaleString() + '원'
})

// 진행 바 퍼센트 계산 (진행률 = amount / budget * 100)
const progressPercentage = computed(()=>{
  const { amount, budget } = props;
  // console.log("budget: ", budget);

  if(!budget || budget === 0) return 0;

  // 퍼센트 계산 (최대 100%)
  const percentage = (amount / budget) * 100
  // 범위 제한
  const clamped = Math.min(percentage, 100)

  // 소수점 1자리까지 / 최대 100% (예산보다 초과해도 100)
  return clamped.toFixed(1)
})

// 예산 초과 감지
const isOverBudget = computed(() =>{
  return props.amount >= props.budget;
})

</script>

<style scoped>
.summary-card {
  padding: 16px;
  border-radius: 16px;
  background-color: #fff;
  max-width: 343px; /* TravelCard와 동일 */
  margin: 0 auto; /* 가운데 정렬 */
  box-sizing: border-box;
  font-family: 'IBM Plex Sans KR', sans-serif;
  box-shadow: 0px 1px 2px #0000000d;
}

.summary-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  /*margin-bottom: 12px;*/
}

.label {
  font-size: 14px;
  color: #AAAAAA;
}

.terminate {
  background-color: #F1F5F9;
  color: #4A4A4A;
  border: none;
  padding: 6px 12px;
  font-size: 12px;
  border-radius: 5px;
  cursor: pointer;
  font-weight: 1000;
  font-family: 'IBM Plex Sans KR', sans-serif;
}

.amount-row {
  display: flex;
  /*justify-content: space-between;*/
  align-items: center;
  margin-bottom: 8px;
}

.amount {
  font-size: 36px;
  font-weight: 1000;
  /*margin-bottom: 8px;*/
  margin-right: 7px;
}

.info-icon{
  color:#AAAAAA;
  width:18px;
  padding-bottom:5px;
}

.budget {
  font-size: 14px;
  color: #666;
}

.progress-bar {
  background-color: #eee;
  height: 10px;
  border-radius: 4px;
  overflow: hidden;
  width: 100%;
}

.progress {
  height: 100%;
  background-color: #5C8EF6;
  border-radius: 4px;
  transition: width 0.3s ease-in-out;
}

.progress.over {
  background-color: #ff6666; /* 빨간색 */
}
</style>