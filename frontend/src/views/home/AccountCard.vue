<script setup>
import { ref, onMounted, computed } from 'vue';
import accountApi from '@/api/accountApi.js';
import { ChevronRight } from 'lucide-vue-next';
import { useRouter } from 'vue-router';

const router = useRouter();

// props로 userId 받아옴
const props = defineProps({
  userId: {
    type: Number,
    required: true
  }
});

const getBankLogo = (bankName) => {
  const safeName = bankName?.replace(/\s/g, '').trim();
  try {
    return new URL(`../../assets/bank-logos/${safeName}.png`, import.meta.url).href;
  } catch (e) {
    return new URL(`../../assets/bank-logos/default.png`, import.meta.url).href;
  }
};

const goToAccountDetail = () => {
  router.push(`/mypage/payment`);
};


const bankName = computed(() => {
  if (!account.value) return '';
  return bankCodeMap[account.value.bankCode] || account.value.bankCode;
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
  '023': 'sc제일은행',
  '027': '한국씨티은행',
  '031': '대구은행',
  '032': '부산은행',
  '034': '광주은행',
  '035': '제주은행',
  '037': '전북은행',
  '039': '경남은행',
  '045': '새마을금고',
  '048': '신용협동조합중앙회',
  '050': '상호저축은행',
  '054': 'hsbc',
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
  '089': 'k뱅크',
  '090': '카카오뱅크',
  '092': '토스뱅크',
  '209': '유안타증권',
  '218': 'kb증권',
  '238': '미래에셋증권',
  '240': '삼성증권',
  '243': '한국투자증권',
  '247': 'nh투자',
  '261': '교보증권',
  '262': '하이투자',
  '263': '현대차증권',
  '264': '키움증권',
  '265': '이베스트투자증권',
  '266': 'sk증권',
  '267': '대신',
  '269': '한화투자증권',
  '270': '하나금융투자',
  '278': '신한금융투자',
  '279': 'db금융투자',
  '280': '유진투자증권',
  '287': '메리츠종합금융증권',
  '291': '신영증권'
};

</script>

<template>
  <div class="account-card" v-if="account" @click="goToAccountDetail">
    <div class="top-row">
      <div class="left">
        <img :src="getBankLogo(bankName)" class="bank-logo" />
        <span class="account-number">{{ account.accountNumber }}</span>
      </div>
      <ChevronRight class="chevron-icon"/>
    </div>
    <div class="bottom-row">
      <span class="balance">{{ formattedBalance }}</span>
    </div>
  </div>
</template>





<style scoped>

.account-card {
  width: 100%;
  background-color: #fff;
  border-radius: 16px;
  box-sizing: border-box;
  padding: 9px;
  box-shadow: 0 2px 6px rgba(0, 0, 0, 0.1);
  display: flex;
  flex-direction: column;
  cursor: pointer;
  transition: background-color 0.2s ease;
}


.top-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.left {
  display: flex;
  align-items: center;
  gap: 8px;
}

.bank-logo {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  object-fit: cover;
}

.account-number {
  font-size: 15px;
  font-weight: 500;
  color: #333;
}

.chevron {
  font-size: 20px;
  color: #bbb;
}

.bottom-row {
  /* 🔥 이걸로 왼쪽 정렬 유지 */
  display: flex;
  align-items: flex-start;
}

.balance {
  font-size: 30px;
  font-weight: bold;
  color: #000;
  margin-left: 5px;
}

.chevron-icon {
  width: 38px;
  height: 20px;
  color: #888;
}



</style>
