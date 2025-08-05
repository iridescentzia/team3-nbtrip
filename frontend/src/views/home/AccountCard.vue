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
  '003': 'gieobbank',
  '004': 'kbbank',
  '011': 'nhbank',
  '020': 'ulibank',
  '023': 'scbank',
  '027': 'kcitibank',
  '081': 'hanabank',
  '088': 'sinhanbank',
  '090': 'kakaobank',
  '092': 'tossbank',
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