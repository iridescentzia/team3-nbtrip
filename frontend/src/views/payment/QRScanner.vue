<script setup>
import merchantApi from '@/api/merchantApi';
import paymentApi from '@/api/paymentApi';
import { ref } from 'vue';
import { QrcodeStream } from 'vue-qrcode-reader';

const merchantID = ref(null);
const merchant = ref('');
const merchantName = ref('');

const payment = ref('');
const reason = ref('');

const isModalVisible = ref(false);
const ModalType = ref(1);

const amount = ref(0);

async function onDetect(result) {
  const raw = result[0]?.rawValue;

  try {
    const parsed = JSON.parse(raw);
    merchantID.value = parsed.merchantID;
    merchant.value = await merchantApi.get(merchantID.value);
    merchantName.value = merchant.value.merchantName;
    isModalVisible.value = true;
  } catch (error) {
    console.error('QR 내용 파싱 오류:', error);
  }
}

function onInit(promise) {
  promise
    .then(() => {
      console.log('카메라 접근 성공!');
    })
    .catch((error) => {
      console.error('카메라 초기화 실패:', error);
    });
}

async function submitPayment() {
  try {
    const payload = {
      tripId: 1, // 임시 tripId, 실제로는 tripId를 받아와야 함
      userId: 1, // 임시 userId, 실제로는 로그인된 사용자 ID를 받아와야 함
      merchantId: merchantID.value,
      amount: amount.value,
      paymentType: 'QR', // QR 결제 타입
    };
    console.log('결제 요청 데이터:', payload);
    payment.value = await paymentApi.create(payload);
    console.log('결제 정보:', payment.value);
    ModalType.value = 2; // 결제 완료 모달
  } catch (error) {
    console.error('결제 실패:', error);
    reason.value = error.response.data || '알 수 없는 오류';
    ModalType.value = 3; // 결제 실패 모달
  }
}

// const load = async () => {};
// load();
</script>

<template>
  <div>
    <div style="text-align: center; margin-top: 10%">
      <h3>QR 스캔</h3>
      <p>결제할 가맹점의 QR코드를 인식해주세요.</p>
      <qrcode-stream
        v-if="!isModalVisible"
        :formats="['qr_code']"
        @detect="onDetect"
        @init="onInit"
      />
    </div>

    <div v-if="isModalVisible && ModalType == 1" class="modal">
      <div class="modal-container">
        <h3>‘{{ tripName }}’ 중</h3>

        <div class="input-group">
          <label for="merchant">가맹점</label>
          <input id="merchant" :value="merchantName" type="text" readonly />
        </div>

        <div class="input-group">
          <label for="amount">결제 금액</label>
          <input
            id="amount"
            v-model="amount"
            type="number"
            placeholder="예: 55000"
          />
        </div>

        <div class="input-group">
          <label>결제에 참여하는 사람들</label>
          <div class="checkbox">
            <input
              type="checkbox"
              id="selectAll"
              v-model="selectAll"
              @change="toggleSelectAll"
            />
            <label for="selectAll">전체 선택</label>
          </div>
          <div
            v-for="(person, index) in participants"
            :key="index"
            class="checkbox"
          >
            <input
              type="checkbox"
              :id="'person-' + index"
              v-model="selectedParticipants"
              :value="person"
            />
            <label :for="'person-' + index">{{ person }}</label>
          </div>
        </div>

        <button @click="submitPayment">결제하기</button>
      </div>
    </div>

    <div v-if="isModalVisible && ModalType == 2" class="modal">
      <div class="modal-container">
        <h3>결제 완료!</h3>
        <!-- 고양이 -->
        <button @click="$router.push('/home')">홈으로 돌아가기</button>
      </div>
    </div>

    <div v-if="isModalVisible && ModalType == 3" class="modal">
      <div class="modal-container">
        <h3>결제에 실패했어요...</h3>
        <!-- 고양이 -->
        <p>사유: {{ reason }}</p>
        <div style="display: flex">
          <button @click="isModalVisible = false">취소하기</button>
          <button @click="submitPayment">다시 시도하기</button>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
/* qrcode-stream {
  width: 100%;
  max-width: 400px;
  margin: auto;
} */

.modal-container {
  width: 300px;
  background: white;
  border-radius: 12px;
  padding: 24px;
  box-shadow: 0 0 20px rgba(0, 0, 0, 0.1);
}

.input-group {
  margin-bottom: 20px;
}

input[type='text'],
input[type='number'] {
  width: 100%;
  padding: 10px;
  border: 1px solid #ddd;
  border-radius: 8px;
}

.checkbox {
  display: flex;
  align-items: center;
  margin: 6px 0;
}

.checkbox input {
  margin-right: 8px;
}

button {
  width: 100%;
  background-color: #fcd265;
  color: black;
  padding: 12px;
  border: none;
  border-radius: 10px;
  font-weight: bold;
  cursor: pointer;
}
</style>
