<script setup>
import { ref } from 'vue';
import { QrcodeStream } from 'vue-qrcode-reader';

const merchantID = ref(null);
const isModalVisible = ref(false);

function onDetect(result) {
  const raw = result[0]?.rawValue;

  try {
    const parsed = JSON.parse(raw);
    merchantID.value = parsed.merchantID;
    // console.log('merchantID:', merchantID.value);
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
      <div class="modal-content">
        모달창
        <button @click="isModalVisible = false">닫기</button>
      </div>
    </div>
  </div>
</template>

<style scoped>
qrcode-stream {
  width: 100%;
  max-width: 400px;
  margin: auto;
}
</style>
