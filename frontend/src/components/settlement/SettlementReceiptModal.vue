<script setup>
import { computed, ref, watch } from 'vue';

const props = defineProps({
  breakdownData: {
    type: Object,
    default: null,
  },
  onClose: {
    type: Function,
    required: true,
  },
});

const showContent = ref(false);

watch(
  () => props.breakdownData,
  (newData) => {
    if (newData) {
      // Use setTimeout to ensure the overlay is rendered before the content animates in
      setTimeout(() => {
        showContent.value = true;
      }, 20);
    } else {
      showContent.value = false;
    }
  }
);

const finalAmountAbs = computed(() => {
  if (!props.breakdownData) return 0;
  return Math.abs(props.breakdownData.finalAmount);
});

const formatDate = (dateString) => {
  if (!dateString) return '';
  const date = new Date(dateString);
  const yyyy = date.getFullYear();
  const MM = String(date.getMonth() + 1).padStart(2, '0');
  const dd = String(date.getDate()).padStart(2, '0');
  const HH = String(date.getHours()).padStart(2, '0');
  const mm = String(date.getMinutes()).padStart(2, '0');
  return `${yyyy}.${MM}.${dd} ${HH}:${mm}`;
};
</script>

<template>
  <div v-if="breakdownData" class="receipt-modal-overlay" @click.self="onClose">
    <Transition name="modal-content-fade">
      <div v-if="showContent" class="modal-content">
        <div class="modal-header">
          <h1 class="modal-title">
            {{ breakdownData.otherUserNickname }}님과의 정산 영수증
          </h1>
          <p class="modal-description">
            💡 검은색은 받을 돈, 빨간색은 보낼 돈이에요!
          </p>
        </div>
        <hr class="divider" />
        <div class="receipt-body">
          <div
            v-for="(item, index) in breakdownData.items"
            :key="index"
            class="receipt-item-container"
          >
            <div class="receipt-line-item">
              <p class="item-title">
                {{ item.title }}
                <span class="item-payer">{{
                  item.payerNickname === breakdownData.myNickname
                    ? ''
                    : '(상대방 결제)'
                }}</span>
              </p>
              <span
                class="item-amount"
                :class="{
                  'amount-positive':
                    item.payerNickname === breakdownData.myNickname,
                  'amount-negative':
                    item.payerNickname === breakdownData.otherUserNickname,
                }"
              >
                {{
                  item.payerNickname === breakdownData.myNickname
                    ? `${item.mySplitAmount.toLocaleString()}원`
                    : `-${Math.abs(item.mySplitAmount).toLocaleString()}원`
                }}
              </span>
            </div>
            <div class="sub-line receipt-line-item">
              <p>{{ formatDate(item.payAt) }}</p>
              <p>총 {{ item.totalAmount.toLocaleString() }}원</p>
            </div>
          </div>
        </div>
        <hr class="divider" />
        <div class="receipt-line-item total-line">
          <span>TOTAL</span>
          <span
            :class="{
              'amount-negative': breakdownData.finalAmount < 0,
              'amount-total': breakdownData.finalAmount >= 0,
            }"
          >
            {{
              breakdownData.finalAmount >= 0
                ? `${finalAmountAbs.toLocaleString()}원`
                : `-${finalAmountAbs.toLocaleString()}원`
            }}
          </span>
        </div>
      </div>
    </Transition>
  </div>
</template>

<style scoped>
p {
  margin: 0;
}

.modal-description {
  font-size: smaller;
  color: #6b7280;
}

.receipt-modal-overlay {
  position: absolute;
  inset: 0;
  background-color: rgba(0, 0, 0, 0.6);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 50;
}

.modal-content {
  width: 90%;
  max-width: 380px;
  background-color: #ffffff;
  border-radius: 0.75rem;
  padding: 1.5rem;
  box-shadow: 0 10px 25px rgba(0, 0, 0, 0.1);
  font-family: 'Pretendard', sans-serif;
  margin: 5%;
  max-height: 80vh; /* Set a max height */
  overflow-y: auto; /* Enable vertical scroll */
}

/* Custom scrollbar for a cleaner look */
.modal-content::-webkit-scrollbar {
  width: 6px;
}
.modal-content::-webkit-scrollbar-track {
  background: #f1f1f1;
  border-radius: 10px;
}
.modal-content::-webkit-scrollbar-thumb {
  background: #c7c7c7;
  border-radius: 10px;
}
.modal-content::-webkit-scrollbar-thumb:hover {
  background: #a8a8a8;
}

.modal-header {
  text-align: center;
}

.modal-title {
  font-size: 1.125rem;
  font-weight: 700;
  color: #1f2937;
}

.receipt-body {
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.receipt-item-container {
  display: flex;
  flex-direction: column;
  margin-bottom: 2px;
  margin-top: 2px;
}

.receipt-line-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  width: 100%;
}

.item-title {
  font-weight: 700;
  color: #1f2937;
  font-size: 0.875rem;
}

.item-payer {
  font-size: 0.75rem;
  font-weight: 400;
  color: #6b7280;
}

.item-amount {
  font-weight: 700;
  font-size: 0.875rem;
}

.sub-line {
  font-size: 0.75rem;
  color: #6b7280;
}

.total-line {
  font-weight: 700;
  font-size: 1rem;
  color: #1f2937;
}

.amount-positive {
  color: #1f2937;
}
.amount-negative {
  color: #ef4444;
}
.amount-total {
  color: #3b82f6;
}

hr.divider {
  border: none;
  border-top: 1px dashed #d1d5db;
  margin-top: 1rem;
  margin-bottom: 1rem;
}

/* Modal Content Transition */
.modal-content-fade-enter-active,
.modal-content-fade-leave-active {
  transition: all 0.6s ease-out;
}

.modal-content-fade-enter-from,
.modal-content-fade-leave-to {
  opacity: 0;
  transform: translateY(10px) scale(0.9);
}
</style>
