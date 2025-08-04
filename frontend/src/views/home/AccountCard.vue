<script setup>
import { ref, onMounted, computed } from 'vue';
import accountApi from '@/api/accountApi.js';

// props로 userId 받아옴
const props = defineProps({
  userId: {
    type: Number,
    required: true
  }
});

// 계좌 정보 상태
const account = ref(null);

// 계좌 조회
onMounted(async () => {
  try {
    const res = await accountApi.getAccountByUserId(props.userId);
    account.value = res;
  } catch (err) {
    console.error('계좌 정보 조회 실패:', err);
  }
});

// 통화 포맷 함수
const formatMoney = (amount) => {
  return amount.toLocaleString() + '원';
};

// computed로 잔액 포맷
const formattedBalance = computed(() => {
  if (!account.value) return '';
  return formatMoney(account.value.balance);
});

const bankCodeMap = {
  '002': '산업은행',
  '003': '기업은행',
  '004': '국민은행',
  '007': '수협은행',
  '011': '농협은행',
  '012': '지역농축협',
  '020': '우리은행',
  '023': 'SC제일은행',
  '027': '한국씨티은행',
  '031': '대구은행',
  '032': '부산은행',
  '034': '광주은행',
  '035': '제주은행',
  '037': '전북은행',
  '039': '경남은행',
  '045': '새마을금고중앙회',
  '048': '신용협동조합중앙회',
  '050': '상호저축은행',
  '054': 'HSBC',
  '055': '도이치뱅크',
  '057': '제이피모건체이스은행',
  '058': '미즈호은행',
  '059': '엠유에프지은행',
  '060': 'BOA은행',
  '062': '중국공상은행',
  '064': '산림조합중앙회',
  '067': '중국건설은행',
  '071': '우체국',
  '081': '하나은행',
  '088': '신한은행',
  '089': 'K뱅크',
  '090': '카카오뱅크',
  '092': '토스뱅크',
  '218': 'KB증권',
  '238': '미래에셋증권',
  '240': '삼성증권',
  '243': '한국투자증권',
  '247': 'NH투자증권',
  '261': '교보증권',
  '262': '하이투자증권',
  '263': '현대차증권',
  '264': '키움증권',
  '265': '이베스트투자증권',
  '266': 'SK증권',
  '267': '대신증권',
  '269': '한화투자증권',
  '270': '하나금융투자',
  '278': '신한금융투자',
  '279': 'DB 금융투자',
  '280': '유진투자증권',
  '287': '메리츠종합금융증권',
  '291': '신영증권'
};

</script>

<template>
  <div class="account-card">
    <div v-if="account" class="account-info">
      <p><strong>은행명:</strong> {{ bankCodeMap[account.bankCode] || account.bankCode }}</p>
      <p><strong>계좌번호:</strong> {{ account.accountNumber }}</p>
      <p><strong>잔액:</strong> {{ formattedBalance }}</p>
    </div>
  </div>
</template>



<style scoped>
.account-card {
  width: 100%;
  background-color: #fff;
  border-radius: 12px;
  box-sizing: border-box;
  padding: 16px;
  box-shadow: 0 2px 6px rgba(0,0,0,0.05);
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.account-info p {
  margin: 6px 0;
  font-size: 16px;
}

.loading {
  color: #888;
  font-size: 14px;
}

</style>
