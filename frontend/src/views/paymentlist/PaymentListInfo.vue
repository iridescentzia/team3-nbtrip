<template>
  <div class="wrapper">
     <!-- computed된 filteredPayments를 순회하며 카드 렌더링 -->
    <ExpenseCard
      v-for="(item, index) in filteredPayments"
      :key="index"
      :title="item.memo"
      :sub="formatSub(item.nickname, item.payAt)"
      :amount="item.amount"
    />
  </div>
</template>

<script setup>
import { ref,computed, onMounted,watch } from 'vue';
import { useRoute } from 'vue-router'; 
import ExpenseCard from '@/views/paymentlist/PaymentListInfoCard.vue';
import paymentApi from '@/api/paymentlistApi';

const props = defineProps({
  dateRange: {
    type: Object,
    default: () => ({ start: '', end: '' })
  },
  selectedParticipants:{
    type:Array,
    default: () => []
  }
})

const emit = defineEmits(['init-total']);

// URL에서 tripId 추출
// paymentlistStore로 분리해야 함
const route = useRoute();
const tripId = Number(route.params.tripId); 
const payments = ref([]); // 서버에서 받아온 전체 결제 내역

const filteredPayments = computed(() => {
  // 날짜 필터
  const { start, end } = props.dateRange
  console.log('[PaymentListInfo.vue] 현재 dateRange:', start, end)

  let result = payments.value;
  console.log('원래 payments 수:', payments.value.length);

  // 날짜 필터
  // 시작/종료 날짜가 모두 비어있으면 전체 반환
  // if (!start && !end) return result
  if(start || end){
      result = result.filter(p => {
      // ISO 문자열에서 yyyy-MM-dd 부분만 비교
      const payDate = new Date(p.payAt)
      const startDate = start ? new Date(start) : null;
      const endDate = end ? new Date(end) : null;

        // 끝 날짜를 하루의 마지막 시각으로 보정
        if (endDate) {
          endDate.setHours(23, 59, 59, 999);
        }

      if (startDate && endDate) return payDate >= startDate && payDate <= endDate;
      if (startDate) return payDate >= startDate;
      if (endDate) return payDate <= endDate;
    });

  }
  console.log('날짜 필터 후 수:', result.length);

  // 결제 참여자 필터
  if (props.selectedParticipants.length > 0) {
    const selectedIds = props.selectedParticipants.map(Number); 
    result = result.filter(p => selectedIds.includes(p.userId));
    console.log("불러온 payments:", payments.value);
    console.log("userId 타입 체크:", payments.value.map(p => typeof p.userId)); // → 'number'만 나오면 OK
  }

  return result;
})

watch(() => props.selectedParticipants, (val) => {
  console.log('[watch] selectedParticipants:', val);
});

watch(() => props.dateRange, (val) => {
  console.log('[watch] dateRange:', val);
});

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
      console.log("result.paymentData: ", result.paymentData)
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
