<script setup>
// import { ref, onMounted } from 'vue';
// import accountApi from '@/api/accountApi.js';

defineProps({
  bankName: String,
  accountNumber: String,
  balance: Number
})

const formatCurrency = (value) => {
  return value.toLocaleString();
}

// const props = defineProps({
//   userId: Number,
// });

// const account = ref(null);

// onMounted(async () => {
//   const res = await accountApi.getAccount(props.userId);
//   console.log('계좌 정보:', res);
//   account.value = res;
// });
</script>

<template>
  <div class="account-card">
    <div class="bank-name">{{ bankName }}</div>
    <div class="account-number">{{ accountNumber }}</div>
    <div class="balance">{{ formatCurrency(balance) }}원</div>
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

.bank-name {
  font-weight: 600;
  font-size: 14px;
  color: #333;
}

.account-number {
  font-size: 13px;
  color: #888;
}

.balance {
  font-size: 16px;
  font-weight: 700;
  color: #4A4A4A;
  margin-top: 4px;
}
</style>
