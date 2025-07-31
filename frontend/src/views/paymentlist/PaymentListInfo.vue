<template>
  <div class="wrapper">
    <ExpenseCard
      v-for="(item, index) in payments"
      :key="index"
      :title="item.memo"
      :sub="formatSub(item.userId, item.payAt)"
      :amount="item.amount"
    />
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { useRoute } from 'vue-router'; // ✅ 라우터에서 파라미터 추출
import ExpenseCard from '@/views/paymentlist/PaymentListInfoCard.vue';
import paymentApi from '@/api/paymentlistApi';

const route = useRoute();
const tripId = Number(route.params.tripId); // ✅ URL에서 tripId 추출
const payments = ref([]);

function formatSub(payer, time) {
  const t = new Date(time);
  const h = t.getHours().toString().padStart(2, '0');
  const m = t.getMinutes().toString().padStart(2, '0');
  const meridiem = t.getHours() < 12 ? '오전' : '오후';
  return `${payer} · ${meridiem} ${h}:${m}`;
}

onMounted(async () => {
  try {
    const result = await paymentApi.getPaymentList(tripId);
    console.log('결제내역 리스트', result);
    console.log('tripId', tripId);

    if (Array.isArray(result.paymentData)) {
      payments.value = result.paymentData;
    } else {
      console.warn('paymentData가 배열이 아님', result);
    }
  } catch (e) {
    console.error('결제 내역 불러오기 실패:', e);
  }
});
</script>

<style scoped>
.wrapper {
  padding: 20px;
  background-color: #f5f5f5;
  min-height: 100vh;
}
</style>
