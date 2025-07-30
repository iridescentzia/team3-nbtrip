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
    };
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
          <h2>QR 스캔</h2>
          <p>결제할 가게의 QR코드를 인식해주세요.</p>
          <QRScanner />

          <div
            v-if="store.isModalVisible && store.modalType === 1"
            class="modal"
          >
            <!-- 결제 입력 모달 -->
            <h2>'{{ store.tripName }}' 중</h2>

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
                v-for="(participant, index) in store.participantsId"
                :key="index"
                class="checkbox-group"
              >
                <input
                  type="checkbox"
                  :id="`participant-${index}`"
                  :value="participant"
                  v-model="store.participantsId"
                />
                <label :for="`user-${index}`">{{ participant }}</label>
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
            <button @click="$router.push('/')">홈으로</button>
          </div>

          <!-- 결제 실패 모달 -->
          <div
            v-if="store.isModalVisible && store.modalType === 3"
            class="modal"
          >
            <h3>결제에 실패했어요...</h3>
            <p>사유: {{ store.reason }}</p>
            <button @click="store.resetModal()">닫기</button>
          </div>
        </div>
      </main>
      <div class="mid"></div>
    </div>
  </div>
</template>

<style scoped>
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
</style>
