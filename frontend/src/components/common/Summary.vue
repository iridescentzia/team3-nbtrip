<template>
  <div class="summary-card">
    <div class="summary-header">
      <span class="label">총 사용 금액</span>
      <button
          v-if="isOwner"
          class="terminate"
          small
          @click="openTerminateModal"
      >
        여행 종료하기
      </button>
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

  <!-- 여행 종료 모달 -->
  <div
      v-if="showTerminateModal"
      class="modal-overlay"
      @click="cancelTerminate"
  >
    <div class="terminate-modal" @click.stop>
      <!-- 아이콘 -->
      <div class="modal-icon"></div>

      <!-- 메인 메시지 -->
      <h3 class="modal-title">정말 여행이 끝났나요?</h3>

      <!-- 설명 텍스트 -->
      <p class="modal-description">
        정산 요청하러 바로 넘어갈게요!
      </p>

      <!-- 버튼들 -->
      <div class="modal-buttons">
        <button @click="cancelTerminate" class="modal-cancel-btn">취소</button>
        <button @click="confirmTerminate" class="modal-confirm-btn">정산 요청하기</button>
      </div>
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
  },
  onTerminate: {
    type: Function,
    required: false
  },
  isOwner: {
    type: Boolean,
    default: false
  }
})

// 상태: info 팝업 토글
const showInfo = ref(false)
const toggleInfo = () => {
  showInfo.value = !showInfo.value
}

// 상태: 여행 종료 모달
const showTerminateModal = ref(false)

// 여행 종료 모달 열기
const openTerminateModal = () => {
  showTerminateModal.value = true
}

// 여행 종료 모달 취소
const cancelTerminate = () => {
  showTerminateModal.value = false
}

// 여행 종료 확인
const confirmTerminate = () => {
  showTerminateModal.value = false
  if (props.onTerminate) {
    props.onTerminate()
  }
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

/* 모달 스타일 (DetailView와 동일) */
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100vw;
  height: 100vh;
  background-color: rgba(0, 0, 0, 0.6);
  display: flex;
  justify-content: center;
  align-items: flex-end;
  z-index: 1100;
  animation: fadeIn 0.3s ease-out;
  backdrop-filter: blur(2px);
  -webkit-backdrop-filter: blur(2px);
}

.terminate-modal {
  width: 100%;
  max-width: 325px;
  height: auto;
  min-height: 230px;
  background: white;
  border-radius: 1.5rem;
  box-shadow: 0px -4px 32px rgba(0, 0, 0, 0.24);
  padding: 28px 40px 36px 40px;
  position: relative;
  animation: slideUpFromBottom 0.3s ease-out;
}

.modal-icon {
  width: 40px;
  height: 40px;
  font-size: 28px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 0 auto 20px auto;
}

.modal-title {
  text-align: center;
  color: #1f2937;
  font-size: 20px;
  font-weight: 700;
  line-height: 28px;
  margin: 0 0 16px 0;
}

.modal-description {
  text-align: center;
  color: #6b7280;
  font-size: 14px;
  font-weight: 500;
  line-height: 20px;
  margin: 0 0 28px 0;
}

.modal-buttons {
  display: flex;
  gap: 12px;
  justify-content: center;
  width: 100%;
  max-width: 400px;
  margin: 0 auto;
}

.modal-cancel-btn,
.modal-confirm-btn {
  flex: 1;
  height: 48px;
  background: rgba(255, 209, 102, 0.65);
  border-radius: 12px;
  border: none;
  color: #374151;
  font-size: 16px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.2s ease;
  display: flex;
  align-items: center;
  justify-content: center;
}

.modal-cancel-btn:hover,
.modal-confirm-btn:hover {
  opacity: 0.8;
  transform: translateY(-1px);
}

.modal-cancel-btn:active,
.modal-confirm-btn:active {
  transform: translateY(0);
}

/* 애니메이션 */
@keyframes fadeIn {
  from {
    opacity: 0;
  }
  to {
    opacity: 1;
  }
}

@keyframes slideUpFromBottom {
  from {
    transform: translateY(100%);
    opacity: 0;
  }
  to {
    transform: translateY(0);
    opacity: 1;
  }
}

/* 반응형 대응 */
@media (max-width: 480px) {
  .terminate-modal {
    padding: 24px 20px 32px 20px;
    margin: 0 auto;
    border-radius: 16px;
    margin-bottom: 2rem;
  }

  .modal-buttons {
    gap: 8px;
    max-width: none;
  }

  .modal-cancel-btn,
  .modal-confirm-btn {
    height: 44px;
    font-size: 15px;
  }
}

@media (min-width: 768px) {
  .terminate-modal {
    max-width: 325px;
    margin: 0 auto;
    border-radius: 16px;
    margin-bottom: 2rem;
  }
}
</style>