<template>
  <div class="wrapper" @click="$emit('click')">
    <!-- computed된 filteredPayments를 순회하며 카드 렌더링 -->
    <ExpenseCard
      v-for="(item, index) in filteredPayments"
      :key="index"
      class="expense-card-item"
      :title="item.paymentType == 'QR' ? item.merchantName : item.memo"
      :sub="formatSub(item.nickname, item.payAt)"
      :amount="item.amount"
      @click="goToUpdate(item.paymentId)"
    />
    <!-- 결제 내역이 없는 경우 -->
    <!-- <div v-if="filteredPayments.length === 0 && isPaymentTab">
      결제 내역이 없습니다.
    </div> -->
  </div>
</template>

<script setup>
import { ref, computed, onMounted, watch } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import ExpenseCard from '@/views/paymentlist/PaymentListInfoCard.vue';
import paymentlistApi from '@/api/paymentlistApi';

const props = defineProps({
  dateRange: {
    type: Object,
    default: () => ({ start: '', end: '' }),
  },
  selectedParticipants: {
    type: Array,
    default: () => [],
  },
  selectedCategories: {
    type: Array,
    default: () => [],
  },
  activeTab: String,
});

const emit = defineEmits(['init-total']);

// URL에서 tripId 추출
// paymentlistStore로 분리해야 함
const route = useRoute();
const router = useRouter();
const tripId = Number(route.params.tripId);
const payments = ref([]); // 서버에서 받아온 전체 결제 내역

// 필터링된 결제 내역
const filteredPayments = computed(() => {
  const { start, end } = props.dateRange;
  // console.log('[PaymentListInfo.vue] 현재 dateRange:', start, end)

  let result = payments.value;
  console.log('원래 payments 수:', payments.value.length);

  // 탭 필터
  if (props.activeTab === '그룹 지출 내역') {
    result = result.filter(
      (p) => p.paymentType === 'QR' || p.paymentType === 'OTHER'
    );
    console.log('[DEBUG] 탭 필터 후:', result.length);
  } else if (props.activeTab === '선결제 내역') {
    result = result.filter((p) => p.paymentType === 'PREPAID');
    console.log('[DEBUG] 탭 필터 후:', result.length);
  }

  // 날짜 필터
  // 시작/종료 날짜가 모두 비어있으면 전체 반환
  // if (!start && !end) return result
  if (start || end) {
    result = result.filter((p) => {
      // ISO 문자열에서 yyyy-MM-dd 부분만 비교
      const payDate = new Date(p.payAt);
      const startDate = start ? new Date(start) : null;
      const endDate = end ? new Date(end) : null;

      // 끝 날짜를 하루의 마지막 시각으로 보정
      if (endDate) {
        endDate.setHours(23, 59, 59, 999);
      }

      if (startDate && endDate)
        return payDate >= startDate && payDate <= endDate;
      if (startDate) return payDate >= startDate;
      if (endDate) return payDate <= endDate;

      console.log('날짜 필터링 후 수:', result.length);
    });
  }

  // 결제 참여자 필터
  if (props.selectedParticipants.length > 0) {
    const selectedIds = props.selectedParticipants.map(String);

    // 디버깅 로그
    result.forEach((p) => {
      const matched =
        Array.isArray(p.participants) &&
        p.participants.some((participant) =>
          selectedIds.includes(String(participant.userId))
        );
      // console.log(`[FILTER] paymentId=${p.paymentId}, matched=${matched}, participants=`, p.participants);
    });
    result = result.filter(
      (p) =>
        Array.isArray(p.participants) && // participants가 undefined일 경우 방지
        p.participants.some(
          (
            participant // participants 중 하나라도 해당되면 true
          ) => selectedIds.includes(String(participant.userId))
        )
    );
    console.log('결제 참여자 필터링 후 수:', result.length);
  }

  // 카테고리 필터
  if (props.selectedCategories.length > 0) {
    result = result.filter((p) =>
      props.selectedCategories.includes(p.categoryId)
    );
    console.log('카테고리 필터링 후 수:', result.length);
  }

  return result;
});

watch(
  () => props.selectedParticipants,
  (val) => {
    console.log('[watch] selectedParticipants:', val);
  }
);

watch(
  () => props.dateRange,
  (val) => {
    console.log('[watch] dateRange:', val);
  }
);

watch(
  () => props.selectedCategories,
  (val) => {
    console.log('[watch] selectedCategories:', val);
  }
);

function formatSub(payer, payAt) {
  const time = new Date(payAt);

  const year = time.getFullYear().toString();
  const month = (time.getMonth() + 1).toString().padStart(2, '0');
  const day = time.getDate().toString().padStart(2, '0');

  const hour = time.getHours().toString().padStart(2, '0');
  const minute = time.getMinutes().toString().padStart(2, '0');

  return `${payer} · ${year}-${month}-${day} ${hour}:${minute}`;
}

// 필터와 무관한 전체 지출 총액
const originalTotalAmount = computed(() =>
  payments.value.reduce((sum, item) => sum + item.amount, 0)
);

function goToUpdate(paymentId) {
  router.push(`/paymentlist/update/${paymentId}`);
}

// 컴포넌트 마운트 시 서버에서 결제 내역 호출
onMounted(async () => {
  try {
    const result = await paymentlistApi.getPaymentList(tripId); // api/paymentlist/${tripid}
    console.log('결제내역 리스트', result);
    console.log('tripId', tripId);

    if (Array.isArray(result.paymentData)) {
      payments.value = result.paymentData;
      emit('init-total', originalTotalAmount.value);
      console.log('result.paymentData: ', result.paymentData);
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
  /* min-height: 100vh; */
}
.expense-card-item {
  transition: transform 0.3s ease, box-shadow 0.3s ease;
}
.expense-card-item:hover {
  transform: translateY(-3px) scale(1.02);
  box-shadow: 0 8px 16px rgba(0, 0, 0, 0.1);
}
</style>
