<script setup>
import { ref } from 'vue';
import { QrcodeStream } from 'vue-qrcode-reader';

const merchantID = ref(null);

function onDetect(result) {
  const raw = result[0]?.rawValue;

  try {
    const parsed = JSON.parse(raw);
    merchantID.value = parsed.merchantID;
    console.log('merchantID:', merchantID.value);
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
    <qrcode-stream :formats="['qr_code']" @detect="onDetect" @init="onInit" />
    <p>인식된 merchantID: {{ merchantID }}</p>
  </div>
</template>

<style scoped>
qrcode-stream {
  width: 100%;
  max-width: 400px;
  margin: auto;
}
</style>
