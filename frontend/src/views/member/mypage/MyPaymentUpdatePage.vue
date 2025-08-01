<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import Header from '@/components/layout/Header.vue'
import Button from '@/components/common/Button.vue'
import { getMyInfo } from '@/api/memberApi.js'
// import { getAccount, updateAccount } from '@/api/accountApi.js'
// import { getBankList } from '@/api/bankApi.js'

const router = useRouter()
const userId = ref(null)

// 입력 필드
const accountNumber = ref('')
const bankName = ref('')
// const bankList = ref([])

// 더미데이터 사용
const bankList = ref([
  { code: '003', name: '기업은행' },
  { code: '004', name: '국민은행' },
  { code: '011', name: '농협은행' },
  { code: '020', name: '우리은행' },
  { code: '023', name: 'SC제일은행' },
  { code: '027', name: '한국시티은행' },
  { code: '081', name: '하나은행' },
  { code: '088', name: '신한은행' },
  { code: '090', name: '카카오뱅크' },
  { code: '092', name: '토스뱅크' }
])

// 메시지
const errorMessage = ref('')
const successMessage = ref('')

// 마운트 시 사용자 및 계좌 정보 로딩
onMounted(async () => {
  try {
    const userRes = await getMyInfo()
    userId.value = userRes.data.userId

    // const accountRes = await getAccount(userId.value)
    // accountNumber.value = accountRes.data.accountNumber
    // bankName.value = convertCodeToName(accountRes.data.bankCode)

    // const bankRes = await getBankList()
    // bankList.value = bankRes // [{ code, name }]

    // 더미 데이터 사용
    accountNumber.value = '00000000000000'
    bankName.value = '국민은행'
  } catch (err) {
    errorMessage.value = err.message || '계좌 정보를 불러오지 못했습니다.'
  }
})

// 은행 코드 → 이름 변환
const convertCodeToName = (code) => {
  const bank = bankList.value.find(b => b.code === code)
  return bank?.name || ''
}

// 저장 버튼 클릭
const saveAccount = async () => {
  try {
    // await updateAccount(userId.value, {
    //   accountNumber: accountNumber.value,
    //   bankName: bankName.value
    // })
    console.log('테스트용 저장됨: ', { userId: userId, accountNumber: accountNumber.value, bankName: bankName.value })
    successMessage.value = '계좌 정보가 저장되었습니다.'
    errorMessage.value = ''
  } catch (err) {
    errorMessage.value = err.message || '저장에 실패했습니다.'
    successMessage.value = ''
  }
}
</script>

<template>
  <div class="page-wrapper">
    <Header title="결제 수단 관리" :back-action="() => router.back()" />

    <div class="form">
      <label class="label">은행</label>
      <select v-model="bankName" class="select-box">
        <option disabled value="">은행을 선택하세요</option>
        <option v-for="bank in bankList" :key="bank.code" :value="bank.name">
          {{ bank.name }}
        </option>
      </select><br>

      <label class="label">계좌 번호</label>
      <input
          class="input-box"
          v-model="accountNumber"
          placeholder="계좌 번호 입력"
      />

      <Button label="저장하기" @click="saveAccount" />

      <div v-if="successMessage" class="success-message">{{ successMessage }}</div>
      <div v-if="errorMessage" class="error-message">{{ errorMessage }}</div>
    </div>
  </div>
</template>

<style scoped>
.page-wrapper {
  width: 100%;
  height: 100%;
  background: #f8fafc;
  display: flex;
  flex-direction: column;
}

.form {
  flex: 1;
  display: flex;
  flex-direction: column;
  padding-top: calc(56px + 24px); /* Header 높이만큼 여백 */
  padding-left: 24px;
  padding-right: 24px;
}

.label {
  font-weight: 600;
  color: #333;
  font-size: 14px;
}

.input-box,
.select-box {
  height: 52px;
  border: 2px solid #e2e8f0;
  border-radius: 12px;
  padding: 0 16px;
  font-size: 16px;
  background-color: white;
}

.success-message {
  color: green;
  font-size: 14px;
}

.error-message {
  color: red;
  font-size: 14px;
}
</style>