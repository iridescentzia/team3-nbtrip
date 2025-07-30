<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import Header from '@/components/layout/Header.vue'
import Button from '@/components/common/Button.vue'
import { checkNicknameDuplicate, updateMyInfo, getMyInfo } from '@/api/memberApi.js'

const router = useRouter()

// 사용자 정보 상태
const nickname = ref('')
const name = ref('')
const phoneNumber = ref('')
const email = ref('')
const password = ref('')
const passwordConfirm = ref('')

// 닉네임 중복 확인 상태
const isNicknameChecked = ref(false)
const nicknameValid = ref(false)
const nicknameMessage = ref('')

// 비밀번호 일치 여부
const isPasswordMatch = computed(() => password.value === passwordConfirm.value)

// 뒤로가기
const goBack = () => router.back()

// 마운트 시 기존 사용자 정보 불러오기
onMounted(async () => {
  try {
    const res = await getMyInfo()
    if (res.success) {
      nickname.value = res.data.nickname
      name.value = res.data.name
      phoneNumber.value = res.data.phoneNumber
      email.value = res.data.email
    }
  } catch (err) {
    alert('회원 정보 조회 실패')
  }
})

// 닉네임 중복 확인
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

// 저장 버튼 클릭 시
const submitUpdate = async () => {
  if (!nickname.value || !name.value || !phoneNumber.value || !email.value || !password.value || !passwordConfirm.value) {
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
    const res = await updateMyInfo({
      nickname: nickname.value,
      name: name.value,
      phoneNumber: phoneNumber.value,
      email: email.value,
      password: password.value,
      passwordConfirm: passwordConfirm.value
    })

    if (res.success) {
      alert('회원 정보가 수정되었습니다.')
      router.push('/my') // 수정 완료 후 마이페이지로 이동
    }
  } catch (err) {
    alert(err.message || '회원 정보 수정 실패')
  }
}
</script>

<template>
  <div class="update-container">
    <Header title="회원 정보" :back-action="goBack" />

    <div class="form-area">
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
      <input v-model="phoneNumber" type="text" class="input-box" />

      <!-- 이메일 -->
      <label class="label">이메일</label>
      <input v-model="email" type="email" class="input-box" />

      <!-- 비밀번호 -->
      <label class="label">비밀번호</label>
      <input v-model="password" type="password" class="input-box" />
      <p class="password-rules">
        • 비밀번호는 영문 소문자, 숫자를 포함해 최소 9자리 이상이어야 합니다.<br />
        • 3자 이상의 연속되는 글자, 숫자는 사용이 불가능합니다.
      </p>

      <!-- 비밀번호 확인 -->
      <label class="label">비밀번호 확인</label>
      <input v-model="passwordConfirm" type="password" class="input-box" />
      <div class="password-check">
        <span v-if="passwordConfirm && !isPasswordMatch" class="error">비밀번호가 동일하지 않습니다.</span>
        <span v-if="passwordConfirm && isPasswordMatch" class="success">비밀번호가 동일합니다.</span>
      </div>
    </div>

    <!-- 저장 버튼 -->
    <div class="bottom-fixed">
      <Button label="저장" @click="submitUpdate" />
    </div>
  </div>
</template>

<style scoped>
.update-container {
  position: relative;
  width: 384px;
  height: 800px;
  margin: 0 auto;
  background: #f8fafc;
  border-radius: 24px;
  box-shadow: 0 25px 50px -12px rgba(0, 0, 0, 0.25);
  outline: 1px solid black;
  overflow: hidden;
  display: flex;
  flex-direction: column;
}

.form-area {
  flex: 1;
  overflow-y: auto;
  padding: 24px;
  padding-bottom: 80px;
  box-sizing: border-box;
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

.bottom-fixed {
  position: absolute;
  bottom: 0;
  left: 0;
  width: 100%;
  height: 64px;
  background: #f8f9fa;
  border-top: 1px solid #e2e8f0;

  display: flex;
  align-items: center;
  justify-content: center;
  padding: 0 24px;
  box-sizing: border-box;
}
</style>