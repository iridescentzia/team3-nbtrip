<script setup>
import api from '@/api/merchantApi';
import { ref } from 'vue';
import { QrcodeStream } from 'vue-qrcode-reader';

const merchantID = ref(null);
const merchantName = ref('');
const isModalVisible = ref(false);

function onDetect(result) {
  const raw = result[0]?.rawValue;

  try {
    const parsed = JSON.parse(raw);
    merchantID.value = parsed.merchantID;
    console.log('merchantID:', merchantID.value);
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

const load = async () => {
  merchantName.value = await api.get(merchantID.value);
};
load();
</script>

<template>
  <div>
    <div>
      <h3>QR 스캔</h3>
      <p>결제할 가맹점의 QR코드를 인식해주세요.</p>
      <qrcode-stream
        v-if="!isModalVisible"
        :formats="['qr_code']"
        @detect="onDetect"
        @init="onInit"
      />
    </div>

    <div v-if="isModalVisible" class="modal">
      <div class="modal-container">
        모달창
        <!-- <h3>‘{{ tripName }}’ 중</h3>

        <div class="input-group">
          <label for="merchant">가맹점</label>
          <input
            id="merchant"
            v-model="merchant"
            type="text"
            :placeholder="merchantName"
          />
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

        <button @click="submitPayment">결제하기</button> -->
      </div>
      <!-- <button @click="isModalVisible = false">닫기</button> -->
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
