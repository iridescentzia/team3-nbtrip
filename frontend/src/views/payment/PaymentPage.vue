<script setup>
import { usePaymentStore } from '@/stores/paymentStore';
import paymentApi from '@/api/paymentApi';
import QRScanner from '@/components/qr/QRScanner.vue';
import { getMyInfo } from '@/api/memberApi.js';
import { ref, computed, onMounted, watch } from 'vue';
import { useRouter } from 'vue-router';
import Header from '@/components/layout/Header2.vue';

const router = useRouter();
const store = usePaymentStore();

const canSubmit = computed(() => {
  return store.amount > 0;
});

// 모달이 열릴 때 참여자 모두 선택되도록
watch(
  () => store.isModalVisible && store.modalType === 1,
  (visible) => {
    if (visible) {
      store.selectedParticipants = store.participantsNickname.map(
        (p) => p.userId
      );
    }
  }
);

async function submitPayment() {
  try {
    const payload = {
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
  <div>
    <Header title="QR 스캔" />

    <main class="content">
      <div>
        <div style="text-align: center; margin-bottom: 16px; margin-top: 60px">
          <p>결제할 가게의 QR코드를 인식해주세요.</p>
        </div>

        <QRScanner style="height: 744px" />

        <div v-if="store.isModalVisible && store.modalType === 1" class="modal">
          <!-- 결제 입력 모달 -->
          <h2 style="text-align: center">'{{ store.tripName }}' 중</h2>

          <div class="input-group">
            <label for="merchantName">가게 이름</label><br />
            <input id="merchantName" type="text" :value="store.merchantName" />
          </div>

          <div class="input-group">
            <label for="amount">결제 금액</label><br />
            <div style="display: flex; align-items: center">
              <input
                id="amount"
                :value="store.amount ? store.amount.toLocaleString() : ''"
                @input="
                  (e) => {
                    const val = e.target.value.replace(/[^0-9]/g, '');
                    store.amount = Number(val);
                    e.target.value = val ? Number(val).toLocaleString() : '';
                  }
                "
                type="text"
                placeholder="예: 55,000"
                style="padding-right: 12px; vertical-align: middle; flex: 1"
              />
              <span style="margin-left: 8px">원</span>
            </div>
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
                :checked="
                  store.selectedParticipants.includes(participant.userId)
                "
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
        <div v-if="store.isModalVisible && store.modalType === 2" class="modal">
          <h3>결제 완료!</h3>
          <img
            src="@/assets/img/smiling_cat.png"
            alt="웃는 고양이"
            style="position: absolute; right: 10px; top: 10px; width: 100px"
          />
          <button @click="$router.push('/')">홈으로 돌아가기</button>
        </div>

        <!-- 결제 실패 모달 -->
        <div v-if="store.isModalVisible && store.modalType === 3" class="modal">
          <h3>결제에 실패했어요...</h3>
          <p>사유: {{ store.reason }}</p>
          <img
            src="@/assets/img/crying_cat.png"
            alt="우는 고양이"
            style="position: absolute; right: 10px; top: 10px; width: 100px"
          />
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
</template>

<style scoped>
/* Payment Modal */
/* 모달 박스 */
.modal {
  position: absolute;
  left: 0;
  bottom: 0;
  width: 360px;
  background-color: #ffffff;
  border-radius: 16px 16px 0 0;
  padding: 16px 12px 24px 12px;
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
  display: inline-flex;
  align-items: center;
  min-height: 24px;
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
