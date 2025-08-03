<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import Header from '@/components/layout/Header.vue'
import Button from '@/components/common/Button.vue'
import { getMyInfo } from '@/api/memberApi.js'
import accountApi from '@/api/accountApi.js';

const router = useRouter()
const userId = ref(null)

// 입력 필드
const accountNumber = ref('')
const bankCode = ref('')
const bankList = ref([])

// 메시지
const errorMessage = ref('')
const successMessage = ref('')

// 마운트 시 사용자 및 계좌 정보 로딩
onMounted(async () => {
  try {
    const userRes = await getMyInfo()
    userId.value = userRes.data.userId

    // 은행 목록 조회
    await loadBankList()

    // 계좌 정보 조회
    await loadAccountInfo()

  } catch (err) {
    errorMessage.value = err.message || '정보를 불러오지 못했습니다.'
  }
})

// 은행 목록 조회
const loadBankList = async () => {
  try {
    const banks = await accountApi.getBankList()
    bankList.value = banks
    console.log('은행 목록 조회 성공:', banks)
  } catch (error) {
    console.error('은행 목록 조회 실패:', error)
    // API 실패 시 기본 은행 목록 사용
    bankList.value = [
      { bankCode: '003', bankName: '기업은행' },
      { bankCode: '004', bankName: '국민은행' },
      { bankCode: '011', bankName: '농협은행' },
      { bankCode: '020', bankName: '우리은행' },
      { bankCode: '023', bankName: 'SC제일은행' },
      { bankCode: '027', bankName: '한국시티은행' },
      { bankCode: '081', bankName: '하나은행' },
      { bankCode: '088', bankName: '신한은행' },
      { bankCode: '090', bankName: '카카오뱅크' },
      { bankCode: '092', bankName: '토스뱅크' }
    ]
  }
}

// 계좌 정보 조회
const loadAccountInfo = async () => {
  try {
    const accountInfo = await accountApi.getAccountByUserId(userId.value)
    accountNumber.value = accountInfo.accountNumber
    bankCode.value = accountInfo.bankCode
    console.log('계좌 정보 조회 성공:', accountInfo)
  } catch (error) {
    console.error('계좌 정보 조회 실패:', error)
    if (error.response?.status === 404) {
      // 계좌가 등록되지 않은 경우
      console.log('등록된 계좌가 없습니다.')
    } else {
      errorMessage.value = '계좌 정보를 불러오지 못했습니다.'
    }
  }
}

// 은행명 조회 헬퍼 함수 (표시용)
const getBankName = (code) => {
  const bank = bankList.value.find(b => b.bankCode === code)
  return bank?.bankName || ''
}

// 저장 버튼 클릭
const saveAccount = async () => {
  if (!bankCode.value || !accountNumber.value) {
    errorMessage.value = '은행과 계좌번호를 모두 입력해주세요.'
    successMessage.value = ''
    return
  }

  try {
    // 계좌번호 유효성 검사
    if (!/^\d{10,14}$/.test(accountNumber.value)) {
      errorMessage.value = '계좌번호는 10~14자리 숫자만 입력 가능합니다.'
      successMessage.value = ''
      return
    }

    const accountUpdateDTO = {
      bankCode: bankCode.value,
      accountNumber: accountNumber.value,
      accountAlias: null // 별명은 null로 설정
    }

    await accountApi.updateAccount(userId.value, accountUpdateDTO)
    console.log('계좌 정보 업데이트 성공')

    successMessage.value = '계좌 정보가 저장되었습니다.'
    errorMessage.value = ''
  } catch (err) {
    console.error('계좌 정보 업데이트 실패:', err)
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
      <select v-model="bankCode" class="select-box">
        <option disabled value="">은행을 선택하세요</option>
        <option v-for="bank in bankList" :key="bank.bankCode" :value="bank.bankCode">
          {{ bank.bankName }}
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