<script setup>
import { ref, computed, onMounted, defineEmits } from 'vue'
import { registerMember, checkNicknameDuplicate } from '@/api/memberApi.js'
import Button from "@/components/common/Button.vue";
import {useRouter} from "vue-router";
import Header from "@/components/layout/Header.vue";
// import { fetchBankList, createAccount } from '@/api/accountApi.js'

const router = useRouter()
const emit = defineEmits(['signup-complete'])

// 폼 입력 상태
const nickname = ref('')
const name = ref('')
const phoneNumber = ref('')
const email = ref('')
const password = ref('')
const passwordConfirm = ref('')
const bankCode = ref('')
const accountNumber = ref('')

// FCM 토큰
const fcmToken = ref('dummy-token')  // firebase 연동 전(임시 토큰)
// const fcmToken = ref('')  // firebase 연동 후

// FCM 토큰 받아오기(firebase 연동 후)
// onMounted(async () => {
//   try {
//     const token = await getFcmToken()
//     if (token) {
//       fcmToken.value = token
//       console.log('FCM 토큰 발급 성공:', token)
//     } else {
//       console.warn('FCM 토큰을 가져오지 못했습니다.')
//     }
//   } catch (err) {
//     console.error('FCM 토큰 발급 오류:', err)
//   }
// })

// 닉네임 중복 확인 상태
const isNicknameChecked = ref(false)
const nicknameValid = ref(false)
const nicknameMessage = ref('')

// 비밀번호 일치 여부
const isPasswordMatch = computed(() => password.value === passwordConfirm.value)

// 은행 코드 리스트(account DB)
// const bankCodes = ref([])
const bankCodes = ref([
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

// 은행 목록 불러오기
onMounted(async () => {
  // try {
  //   const data = await fetchBankList()
  //   bankCodes.value = data.banks || []
  // } catch (err) {
  //   alert('은행 목록을 불러오지 못했습니다.')
  // }
})

// 닉네임 중복 확인(POST /api/users/check-nickname)
const checkNickname = async () => {
  if (!nickname.value.trim()) {
    alert('닉네임을 입력해주세요.')
    return
  }
  try {
    const res = await checkNicknameDuplicate(nickname.value)
    nicknameValid.value = true
    nicknameMessage.value = res.message || '사용 가능한 닉네임입니다.'
    isNicknameChecked.value = true
  } catch (err) {
    nicknameValid.value = false
    isNicknameChecked.value = false
    nicknameMessage.value = err.message || '이미 사용 중인 닉네임입니다.'
  }
}

// 회원가입(POST /api/auth/register)
const submitForm = async () => {
  if (!nickname.value || !name.value || !phoneNumber.value ||
      !email.value || !password.value || !passwordConfirm.value ||
      !bankCode.value || !accountNumber.value) {
    alert('모든 항목을 입력해주세요.')
    return
  }
  if (!isNicknameChecked.value || !nicknameValid.value) {
    alert('닉네임 중복 확인을 해주세요.')
    return
  }
  if (!isPasswordMatch.value) {
    alert('비밀번호가 일치하지 않습니다.')
    return
  }
  try {
    const res = await registerMember({
      email: email.value,
      password: password.value,
      passwordConfirm: passwordConfirm.value,
      nickname: nickname.value,
      name: name.value,
      phoneNumber: phoneNumber.value,
      bankCode: bankCode.value,
      accountNumber: accountNumber.value,
      fcmToken: fcmToken.value
    });
    if (res.success) {
      alert('회원가입이 완료되었습니다!')
      emit('signup-complete')
    }
  } catch (err) {
    alert(err.message || '회원가입 실패')
  }
}
</script>


<template>
  <div class="join-container"><br/>
    <Header title="회원 정보 입력" :back-action="() => router.back()" />

    <div class="form-area">
      <div class="title-area">
        <h1>회원 정보 입력</h1>
        <img src="@/assets/img/sitting_cat.png" class="cat" />
      </div>
      <!-- 닉네임 -->
      <label class="label">닉네임</label>
      <div class="nickname-wrapper">
        <input v-model="nickname" type="text" class="nickname-input" />
        <button class="nickname-check-button" @click="checkNickname">중복 확인</button>
      </div>
      <div v-if="nicknameMessage" class="nickname-check-message">
        <span :class="nicknameValid ? 'success' : 'error'">{{ nicknameMessage }}</span>
      </div>

      <!-- 이름 -->
      <label class="label">이름</label>
      <input v-model="name" type="text" class="input-box" />

      <!-- 전화번호 -->
      <label class="label">전화번호</label>
      <input v-model="phoneNumber" type="text" class="input-box" placeholder="010-1234-5678" />

      <!-- 이메일 -->
      <label class="label">이메일</label>
      <input v-model="email" type="email" class="input-box" />

      <!-- 비밀번호 -->
      <label class="label">비밀번호</label>
      <input v-model="password" type="password" class="input-box" />
      <p class="password-rules">
        • 비밀번호는 영문 소문자, 숫자를 포함해 최소 9자리 이상이어야 한다.<br />
        • 3자 이상의 연속되는 글자, 숫자는 사용이 불가능하다.
      </p>

      <!-- 비밀번호 확인 -->
      <label class="label">비밀번호 확인</label>
      <input v-model="passwordConfirm" type="password" class="input-box" />
      <div class="password-check">
        <span v-if="passwordConfirm && !isPasswordMatch" class="error">비밀번호가 동일하지 않습니다.</span>
        <span v-if="passwordConfirm && isPasswordMatch" class="success">비밀번호가 동일합니다.</span>
      </div>

      <!-- 은행 선택 -->
      <label class="label">은행 선택</label>
      <div class="select-box">
        <select v-model="bankCode" class="input-box">
          <option disabled value="">은행을 선택하세요</option>
          <option v-for="bank in bankCodes" :key="bank.code" :value="bank.code">
            {{ bank.name }}
          </option>
        </select>
        <div class="dropdown-icon"></div>
      </div>

      <!-- 계좌번호 -->
      <label class="label">계좌번호</label>
      <input v-model="accountNumber" type="text" class="input-box" />
    </div>

    <!-- 회원가입 버튼 -->
    <div class="bottom-fixed">
      <Button label="회원가입" @click="submitForm" />
    </div>
  </div>
</template>

<style scoped>
/* 전체 컨테이너 */
.join-container {
  width: 384px;
  height: 800px;
  margin: 0 auto;
  background: #f8fafc;
  border-radius: 24px;
  box-shadow: 0 25px 50px -12px rgba(0, 0, 0, 0.25);
  outline: 1px solid black;
  outline-offset: -1px;
  overflow: hidden;
  position: relative;
  display: flex;
  flex-direction: column;
}

.form-area {
  flex: 1;
  overflow-y: auto;
  padding: 24px;
  box-sizing: border-box;
}

.title-area {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-top: 16px;
  margin-bottom: 16px;
}

.title-area h1 {
  font-size: 22px;
  font-weight: 600;
  margin: 0;
}

.title-area .cat {
  width: 80px;
  height: auto;
}

.label {
  font-size: 14px;
  font-weight: 500;
  margin-top: 16px;
  display: block;
}

.input-box {
  width: 100%;
  height: 52px;
  border-radius: 12px;
  border: 2px solid #e2e8f0;
  background: white;
  padding: 0 12px;
  margin-top: 4px;
  box-sizing: border-box;
}

.nickname-wrapper {
  display: flex;
  gap: 8px;
  align-items: center;
}

.nickname-input {
  flex: 1;
  height: 52px;
  padding: 0 12px;
  border: 2px solid #e2e8f0;
  border-radius: 12px;
  background: white;
  box-sizing: border-box;
}

.nickname-check-button {
  width: 84px;
  height: 52px;
  background: #fddf99;
  border: none;
  border-radius: 12px;
  font-size: 14px;
  font-weight: 600;
  color: #2e363a;
  cursor: pointer;
}

.nickname-check-message {
  font-size: 12px;
  margin-top: 4px;
}

.success {
  color: #61a569;
}

.error {
  color: #a76a6a;
}

.password-rules {
  font-size: 10px;
  color: #9a9595;
  margin-top: 8px;
  line-height: 1.5;
}

.password-check {
  font-size: 10px;
  margin-top: 8px;
}
</style>