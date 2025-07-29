<script setup>
import { ref, computed } from 'vue'
import axios from 'axios'

// 폼 입력 상태
const nickname = ref('')
const name = ref('')
const phoneNumber = ref('')
const email = ref('')
const password = ref('')
const passwordConfirm = ref('')
const bankCode = ref('')
const accountNumber = ref('')
const fcmToken = ref('')  // firebase 연동 시

// 닉네임 중복 확인 상태
const isNicknameChecked = ref(false)
const nicknameValid = ref(false)
const nicknameMessage = ref('')

// 비밀번호 일치 여부
const isPasswordMatch = computed(() => password.value === passwordConfirm.value)

// 은행 코드 리스트(account DB)
const bankCodes = ref([
  { code: '003', name: '기업은행' },
  { code: '004', name: '국민은행' },
  { code: '011', name: '농협은행' },
  { code: '020', name: '우리은행' },
  { code: '081', name: '하나은행' },
  { code: '088', name: '신한은행' },
  { code: '090', name: '카카오뱅크' }
])

// 닉네임 중복 확인(POST /api/users/check-nickname)
const checkNickname = async () => {
  if (!nickname.value.trim()) {
    alert('닉네임을 입력해주세요.')
    return
  }
  try {
    const res = await axios.post('/api/users/check-nickname', {
      nickname: nickname.value
    })
    nicknameValid.value = true
    nicknameMessage.value = res.data.message || '사용 가능한 닉네임입니다.'
    isNicknameChecked.value = true
  } catch (err) {
    nicknameValid.value = false
    isNicknameChecked.value = false
    nicknameMessage.value = err.response?.data?.message || '이미 사용 중인 닉네임입니다.'
  }
}

// 회원가입(POST /api/auth/register)
const checkNickname = async () => {
  if (!nickname.value.trim()) {
    alert('닉네임을 입력해주세요.')
    return
  }

  try {
    const res = await axios.post('/api/users/check-nickname', {
      nickname: nickname.value
    })
    nicknameValid.value = true
    nicknameMessage.value = res.data.message || '사용 가능한 닉네임입니다.'
    isNicknameChecked.value = true
  } catch (err) {
    nicknameValid.value = false
    isNicknameChecked.value = false
    nicknameMessage.value = err.response?.data?.message || '이미 사용 중인 닉네임입니다.'
  }
}

// 회원가입 요청(POST /api/auth/register)
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
    const res = await axios.post('/api/auth/register', {
      email: email.value,
      password: password.value,
      passwordConfirm: passwordConfirm.value,
      nickname: nickname.value,
      name: name.value,
      phoneNumber: phoneNumber.value,
      fcmToken: fcmToken.value
    })
    if (res.data.success) {
      alert('회원가입이 완료되었습니다!')
      // router.push('/login')
    }
  } catch (err) {
    alert(err.response?.data?.message || '회원가입 실패')
  }
}
</script>


<template>
  <div class="join-container">
    <div class="page-title">회원 정보 입력</div>
    <img src="@/assets/img/sitting_cat.png" alt="로고 이미지" class="top-image" />

    <div class="form-area">
      <!-- 닉네임 -->
      <label class="label">닉네임</label>
      <div class="nickname-wrapper">
        <input v-model="nickname" type="text" class="input-box short" />
        <button class="check-button" @click="checkNickname">중복 확인</button>
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

    <button class="join-button" @click="submitForm">회원가입</button>
  </div>
</template>

<style scoped>
.join-container {
  width: 384px;
  height: 800px;
  position: relative;
  background: #f8fafc;
  box-shadow: 0px 25px 50px -12px rgba(0, 0, 0, 0.25);
  overflow: hidden;
  border-radius: 24px;
  outline: 1px solid black;
  outline-offset: -1px;
  margin: 0 auto;
}

/* 상단 텍스트 */
.page-title {
  position: absolute;
  top: 65px;
  left: 32px;
  font-size: 28px;
  font-weight: 400;
  line-height: 24px;
}

/* 상단 이미지 */
.top-image {
  position: absolute;
  top: 15px;
  left: 263px;
  width: 95px;
  height: 143px;
}

/* 폼 영역 */
.form-area {
  position: absolute;
  top: 158px;
  left: 32px;
  width: 321px;
  height: 814px;
}

.label {
  font-size: 14px;
  font-weight: 500;
  color: #333;
  margin-top: 12px;
  display: block;
}

.input-box {
  width: 320px;
  height: 52px;
  border-radius: 12px;
  border: 2px solid #e2e8f0;
  background: white;
  padding: 0 12px;
  margin-top: 4px;
  box-sizing: border-box;
}

.input-box.short {
  width: 229px;
  display: inline-block;
  margin-right: 8px;
}

.nickname-wrapper {
  display: flex;
  align-items: center;
  margin-bottom: 12px;
}

.check-button {
  width: 84px;
  height: 52px;
  background: #fddf99;
  border-radius: 12px;
  font-size: 14px;
  font-weight: 600;
  color: #2e363a;
  border: none;
  cursor: pointer;
}

.password-rules {
  font-size: 10px;
  color: #9a9595;
  line-height: 1.5;
  margin-top: 8px;
}

.password-check {
  margin-top: 8px;
  font-size: 10px;
  line-height: 1.5;
}

.password-check .error {
  color: #a76a6a;
}

.password-check .success {
  color: #61a569;
}

.select-box {
  width: 320px;
  height: 52px;
  border-radius: 12px;
  outline: 2px solid #e2e8f0;
  outline-offset: -2px;
  background: white;
  margin-top: 4px;
  position: relative;
}

.dropdown-icon {
  width: 11px;
  height: 7px;
  background: #8d8d8d;
  position: absolute;
  right: 12px;
  top: 50%;
  transform: translateY(-50%);
}

/* 회원가입 버튼 */
.join-button {
  position: absolute;
  bottom: 45px;
  left: 32px;
  width: 320px;
  height: 56px;
  background: #fddf99;
  border-radius: 12px;
  font-size: 16px;
  font-weight: 800;
  color: #2e363a;
  border: none;
  cursor: pointer;
}
</style>