<template>
  <div class="wrapper">
     <!-- computed된 filteredPayments를 순회하며 카드 렌더링 -->
    <ExpenseCard
      v-for="(item, index) in filteredPayments"
      :key="index"
      :title="item.memo"
      :sub="formatSub(item.userId, item.payAt)"
      :amount="item.amount"
    />
  </div>
</template>

<script setup>
import { ref,computed, onMounted } from 'vue';
import { useRoute } from 'vue-router'; 
import ExpenseCard from '@/views/paymentlist/PaymentListInfoCard.vue';
import paymentApi from '@/api/paymentlistApi';

const props = defineProps({
  dateRange: {
    type: Object,
    default: () => ({ start: '', end: '' })
  }
})

const emit = defineEmits(['init-total']);

// URL에서 tripId 추출
// paymentlistStore로 분리해야 함
const route = useRoute();
const tripId = Number(route.params.tripId); 
const payments = ref([]); // 서버에서 받아온 전체 결제 내역

const filteredPayments = computed(() => {
  const { start, end } = props.dateRange
  console.log('[PaymentListInfo.vue] 현재 dateRange:', start, end)

  // 시작/종료 날짜가 모두 비어있으면 전체 반환
  if (!start && !end) return payments.value

  return payments.value.filter(p => {
    // ISO 문자열에서 yyyy-MM-dd 부분만 비교
    const payDate = p.payAt.slice(0, 10)
    if (start && end)   return payDate >= start && payDate <= end
    if (start)          return payDate >= start
    if (end)            return payDate <= end
  })
})

function formatSub(payer, time) {
  const t = new Date(time);
  const h = t.getHours().toString().padStart(2, '0');
  const m = t.getMinutes().toString().padStart(2, '0');
  const meridiem = t.getHours() < 12 ? '오전' : '오후';
  return `${payer} · ${meridiem} ${h}:${m}`;
}

// 필터와 무관한 전체 지출 총액
const originalTotalAmount = computed(()=>
  payments.value.reduce((sum, item) => sum + item.amount, 0)
)

// 컴포넌트 마운트 시 서버에서 결제 내역 호출
onMounted(async () => {
  try {
    const result = await paymentApi.getPaymentList(tripId);
    console.log('결제내역 리스트', result);
    console.log('tripId', tripId);

    if (Array.isArray(result.paymentData)) {
      payments.value = result.paymentData;
      emit('init-total', originalTotalAmount.value)
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
  padding: 20px 0;
  /* background-color: #f5f5f5; */
  min-height: 100vh;
}
</style>
