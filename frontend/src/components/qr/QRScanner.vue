<script setup>
import { QrcodeStream } from 'vue-qrcode-reader';
import { usePaymentStore } from '@/stores/paymentStore';
import merchantApi from '@/api/merchantApi';
import tripApi from '@/api/tripApi';

const store = usePaymentStore();

async function onDetect(result) {
  const raw = result[0]?.rawValue;
  try {
    const parsed = JSON.parse(raw);
    const merchant = await merchantApi.get(parsed.merchantID);
    const trip = await tripApi.getTripDetail(1); // 실제 tripId로 교체

    store.setScannerData({
      merchantID: parsed.merchantID,
      merchantName: merchant.merchantName,
      tripName: trip.tripName,
      participants: trip.members.map((p) => p.userId),
    });
  } catch (error) {
    console.error('QR 파싱 오류:', error);
  }
}

function onInit(promise) {
  promise
    .then(() => console.log('카메라 접근 성공'))
    .catch((err) => console.error('카메라 초기화 실패:', err));
}
</script>

<template>
  <qrcode-stream
    v-if="!store.isModalVisible"
    :formats="['qr_code']"
    @detect="onDetect"
    @init="onInit"
  />
</template>
