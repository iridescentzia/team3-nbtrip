<template>
  <div class="summary-card">
    <div class="summary-header">
      <span class="label">총 사용 금액</span>
      <button class="terminate" small>여행 종료하기</button>
    </div>
    <div class="amount-row">
      <div class="amount">{{formattedAmount}}</div>
      <div class="info-wrapper">
        <!-- info 아이콘 -->
        <Info class="info-icon" @click="toggleInfo"/>
        <!-- 예산 사용 정보 메시지 팝업 -->
        <div 
          v-if="showInfo" 
          class="info-popup"
          v-html="budgetMessage"
        >          
        </div>
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

<script setup >
import {computed, ref} from 'vue'
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

// 상태: info 팝업 토글
const showInfo = ref(false)
const toggleInfo = () => {
  showInfo.value = !showInfo.value
}

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

// 숫자 -> 한국어 단위 변환 함수
function formatKoreanCurrency(amount) {
  if (amount === 0) return '0원'

  const units = [
    { value: 10000, label: '만' },
    { value: 1000, label: '천' },
    { value: 100, label: '백' },
    { value: 10, label: '십' },
  ]

  let result = ''
  let remaining = amount

  for (const unit of units) {
    const unitAmount = Math.floor(remaining / unit.value)
    if (unitAmount > 0) {
      result += `${unitAmount}${unit.label} `
      remaining %= unit.value
    }
  }

  return result.trim() + '원'
}

// 예산 사용 정보 메시지 생성
const budgetMessage = computed(()=>{
  const diff = props.budget - props.amount
  const formattedDiff = formatKoreanCurrency(Math.abs(diff))
  if (diff > 0) {
    return `💡예산보다 ${diff.toLocaleString()}원<br> 아끼고 있어요.`
  } else if (diff < 0) {
    return `⚠️예산보다 ${Math.abs(diff).toLocaleString()}원<br> 더 썼어요.`
  } else {
    return '✅예산을 딱 맞췄어요!'
  }
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

.info-wrapper{
  position:relative;
}

.info-icon{
  color:#AAAAAA;
  width:18px;
  /* padding-bottom:5px; */
  cursor: pointer;
  transform: translateY(3px); /* 3px 아래로 */  
}

.info-popup {
  position: absolute;
  top: 24px;
  left: -100px;
  transform: translateY(10px);
  background-color: #fff;
  border: 1px solid #ddd;
  border-radius: 8px;
  padding: 12px 16px; 
  font-size: 14px;     
  line-height: 1.6;    /*  줄 간격 여유 있게 */
  min-width: 170px;    /*  최소 너비 확보 */
  max-width: 240px;    /*  너무 길지 않게 제한 */
  white-space: normal; /*  줄바꿈 허용 */
  box-shadow: 0 2px 8px rgba(0,0,0,0.1);
  z-index: 10;
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