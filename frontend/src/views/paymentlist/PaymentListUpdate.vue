<script setup>
import { ref, onMounted } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import Header from '@/components/layout/Header.vue';
import VueDatePicker from '@vuepic/vue-datepicker';
import { useTripStore } from '@/stores/trip';
import paymentlistApi from '@/api/paymentlistApi';
import paymentApi from '@/api/paymentApi';

const route = useRoute();
const router = useRouter();
const paymentId = route.params.paymentId;
const tripStore = useTripStore();

const date = ref(new Date());
const form = ref({
  content: '',
  amount: '',
  category: '',
  payerUserId: '',
  memo: ''
});
const selectedMembers = ref([]);

const isSelected = (userId) => selectedMembers.value.includes(userId);
const toggleMember = (userId) => {
  const index = selectedMembers.value.indexOf(userId);
  if (index === -1) {
    selectedMembers.value.push(userId);
  } else {
    selectedMembers.value.splice(index, 1);
  }
};

const formatAmountInput = () => {
  const numberOnly = form.value.amount.replace(/[^0-9]/g, '');
  form.value.amount = Number(numberOnly).toLocaleString();
};

const formatDateTime = (date) => {
  const pad = (n) => n.toString().padStart(2, '0');
  return `${date.getFullYear()}-${pad(date.getMonth() + 1)}-${pad(date.getDate())} ${pad(date.getHours())}:${pad(date.getMinutes())}:${pad(date.getSeconds())}`;
};

onMounted(async () => {
  await tripStore.fetchTrips();
  await tripStore.fetchCurrentTripMemberNicknames();
  await tripStore.fetchMerchantCategories();

  try {
    const data = await paymentlistApi.getPaymentListById(paymentId);
    const p = data.payment;

    form.value = {
      content: p.merchantName,
      amount: Number(p.amount).toLocaleString(),
      category: p.categoryId,
      payerUserId: p.userId,
      memo: p.memo
    };
    date.value = new Date(p.payAt);

    // 참여자 목록 별도 API 필요 시 여기에 추가
    // selectedMembers.value = await fetchParticipants(paymentId);

  } catch (e) {
    console.error('결제 내역 불러오기 실패:', e);
  }
});

const handleSave = async () => {
  const amount = parseInt(form.value.amount.replace(/,/g, ''), 10);
  const split = Math.floor(amount / selectedMembers.value.length);
  const tripId = tripStore.currentTrip?.tripId;

  if (!tripId || !form.value.content || !amount || !form.value.category || !form.value.payerUserId || selectedMembers.value.length === 0) {
    console.error('모든 항목을 입력해주세요.');
    return;
  }

  const payAt = formatDateTime(date.value);
  const payload = {
    merchantId: Number(form.value.category),
    amount,
    memo: form.value.memo,
    paymentDate: payAt.split(' ')[0],
    paymentTime: payAt.split(' ')[1],
    paymentType: 'PREPAID',
    participants: selectedMembers.value.map(userId => ({
      userId,
      splitAmount: split
    }))
  };

  try {
    await paymentApi.updateOtherPayment(paymentId, payload);
    console.log('결제 내역 수정 완료!');
    // router.push('/paymentlist');
  } catch (e) {
    console.error('수정 실패:', e.response?.data || e);
  }
};
</script>
