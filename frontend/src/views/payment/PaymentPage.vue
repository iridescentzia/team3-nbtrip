<script setup>
import { usePaymentStore } from '@/stores/paymentStore';
import paymentApi from '@/api/paymentApi';
import QRScanner from '@/components/qr/QRScanner.vue';
import { computed } from 'vue';

const store = usePaymentStore();

const canSubmit = computed(() => {
  return store.amount > 0;
});

async function submitPayment() {
  try {
    const payload = {
      tripId: 1,
      userId: 1,
      merchantId: store.merchantID,
      amount: store.amount,
      paymentType: 'QR',
      participants: store.selectedParticipants.map((id) => ({ userId: id })),
    };
    console.log('결제 요청 데이터:', payload);
    store.payment = await paymentApi.create(payload);
    store.modalType = 2;
  } catch (error) {
    store.reason = error.response?.data || '알 수 없는 오류';
    store.modalType = 3;
  }
}
</script>

<template>
  <div class="layout-wrapper">
    <div class="layout-container">
      <main class="content">
        <div>
          <div style="text-align: center; margin-bottom: 16px">
            <h2>QR 스캔</h2>
            <p>결제할 가게의 QR코드를 인식해주세요.</p>
          </div>

          <QRScanner />

          <div
            v-if="store.isModalVisible && store.modalType === 1"
            class="modal"
          >
            <!-- 결제 입력 모달 -->
            <h2 style="text-align: center">'{{ store.tripName }}' 중</h2>

            <div class="input-group">
              <label for="merchantName">가게 이름</label><br />
              <input
                id="merchantName"
                type="text"
                :value="store.merchantName"
              />
            </div>

            <div class="input-group">
              <label for="amount">결제 금액</label><br />
              <input
                id="amount"
                v-model.number="store.amount"
                type="number"
                placeholder="예: 55000"
              />
            </div>

            <div class="input-group">
              <label>결제에 참여하는 사람들</label>
              <div
                v-for="participant in store.participantsNickname"
                :key="participant.userId"
                class="checkbox-group"
              >
                <input
                  type="checkbox"
                  :id="`participant-${participant.userId}`"
                  :value="participant.userId"
                  v-model="store.selectedParticipants"
                />
                <label :for="`participant-${participant.userId}`">{{
                  participant.nickname
                }}</label>
              </div>
            </div>

            <button @click="submitPayment" :disabled="!canSubmit">
              결제하기
            </button>
          </div>

          <!-- 결제 성공 모달 -->
          <div
            v-if="store.isModalVisible && store.modalType === 2"
            class="modal"
          >
            <h3>결제 완료!</h3>
            <button @click="$router.push('/')">홈으로 돌아가기</button>
          </div>

          <!-- 결제 실패 모달 -->
          <div
            v-if="store.isModalVisible && store.modalType === 3"
            class="modal"
          >
            <h3>결제에 실패했어요...</h3>
            <p>사유: {{ store.reason }}</p>
            <div style="display: flex">
              <button @click="store.resetModal()" style="margin-right: 8px">
                취소하기
              </button>
              <button @click="submitPayment">다시 시도하기</button>
            </div>
          </div>
        </div>
      </main>
      <div class="mid"></div>
    </div>
  </div>
</template>

<style scoped>
/* Default Layout */
.layout-wrapper {
  display: flex;
  justify-content: center;
  width: 100%;
  min-height: 100vh;
  background-color: #f9fafb; /* 기존 Tailwind 'bg-gray-50' */
  position: relative;
  overflow: hidden;
}

.content {
  padding-top: 56px;
  width: 100%;
  max-width: 414px;
  flex: 1;
  padding: 16px;
  box-sizing: border-box;
  display: flex;
  flex-direction: column;
  align-items: center; /* 자식 요소들을 가운데 정렬 */
}

.layout-container {
  width: 100%;
  max-width: 414px; /* 모바일 기준 */
  position: relative;
  padding-top: 0px;
  margin-top: 0px;
}

.mid {
  height: 250px;
}

/* Payment Modal */
/* 모달 박스 */
.modal {
  position: fixed;
  left: 50%;
  bottom: 0;
  transform: translateX(-50%);
  width: 100%;
  max-width: 414px;
  background-color: #ffffff;
  border-radius: 16px 16px 0 0;
  padding: 24px 24px 32px 24px;
  box-shadow: 0 -4px 16px rgba(0, 0, 0, 0.15);
  z-index: 1001;
  animation: modalUp 0.25s ease;
}

@keyframes modalUp {
  from {
    bottom: -300px;
    opacity: 0;
  }
  to {
    bottom: 0;
    opacity: 1;
  }
}

/* 입력 그룹 */
.input-group {
  margin-bottom: 16px;
}

.input-group label {
  font-size: 14px;
  color: #333333;
  margin-bottom: 6px;
}

.input-group input[type='text'],
.input-group input[type='number'] {
  width: 100%;
  padding: 10px 12px;
  font-size: 14px;
  border: 1px solid #cccccc;
  border-radius: 4px;
  box-sizing: border-box;
  margin-top: 8px;
  border-radius: 0.6rem;
}

/* 체크박스 그룹 */
.checkbox-group {
  display: flex;
  align-items: center;
  margin-bottom: 8px;
}

.checkbox-group input[type='checkbox'] {
  appearance: none; /* 기본(네이티브) 모양을 제거 */
  box-sizing: border-box;
  background-clip: content-box;
  width: 16px;
  height: 16px;
  border: 1px solid gray;
  cursor: pointer;
  margin-right: 8px;
}

.checkbox-group input[type='checkbox']:checked {
  border-color: #ffd166;
  background-color: #ffd166;
}

.checkbox-group label {
  margin-top: 6px;
  font-size: 14px;
  color: #333333;
  cursor: pointer;
}

/* 버튼 스타일 */
button {
  width: 100%;
  padding: 12px 0;
  font-size: 16px;
  color: #ffffff;
  background-color: #ffd166;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  transition: background-color 0.2s ease;
}

button:disabled {
  background-color: #cccccc;
  cursor: not-allowed;
}
</style>
