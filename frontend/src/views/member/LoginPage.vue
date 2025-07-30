<script setup>
import { ref, computed } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import JoinPage from './JoinPage.vue'
import {loginMember} from "@/api/memberApi.js";
import DefaultLayout from "@/components/layout/DefaultLayout.vue";

const router = useRouter()
const authStore = useAuthStore()

// 회원가입 전환 여부
const isSignupMode = ref(false)

// 로딩 상태
const isLoading = ref(false)

// 로그인 폼
const loginForm = ref({
  email: '',
  password: ''
})

// 로그인 유효성 검사
const isLoginFormValid = computed(() =>
    loginForm.value.email && loginForm.value.password
)

// FCM 토큰
// firebase 연동 전
const getFcmToken = async () => {
  try {
    return ''
  } catch (error) {
    console.error('FCM 토큰 오류:', error)
    return ''
  }
}

// firebase 연동 후
// const getFcmToken = async () => {
//   try {
//     const token = await getToken(messaging, {
//       vapidKey: 'YOUR_PUBLIC_VAPID_KEY'  // Firebase Console에서 발급받은 VAPID 키
//     })
//
//     if (!token) {
//       console.warn('FCM 토큰을 받아오지 못했습니다.')
//       return ''
//     }
//
//     console.log('FCM 토큰 발급:', token)
//     return token
//   } catch (error) {
//     console.error('FCM 토큰 발급 중 오류:', error)
//     return ''
//   }
// }

// 로그인 처리 함수
const handleLogin = async () => {
  if (!isLoginFormValid.value) {
    alert('이메일과 비밀번호를 입력해주세요.')
    return
  }

  try {
    isLoading.value = true

    const fcmToken = await getFcmToken()
    const response = await loginMember({
      email: loginForm.value.email,
      password: loginForm.value.password,
      fcmToken
    })

    const token = response.accessToken || response.token
    const user = response.user || response.member

    localStorage.setItem('accessToken', token)
    authStore.setToken(token)
    authStore.setUser(user)

    alert('로그인 성공!')
    router.push('/')  // 메인 홈 화면으로 이동
  } catch (error) {
    alert(error.message || '로그인에 실패했습니다.')
    console.error('로그인 오류:', error)
  } finally {
    isLoading.value = false
  }
}
</script>

<template>
  <!-- 회원가입 모드일 경우 JoinPage 컴포넌트 렌더링 -->
  <JoinPage v-if="isSignupMode" @signup-complete="isSignupMode = false"/>

  <!-- 로그인 화면 -->
  <div v-else class="join-container">
    <img src="@/assets/img/logo.png" alt="로고" class="logo" />
    <div class="subtitle">돈 걱정 말고, 여행 가자옹!</div>

    <div class="form-area">
      <label class="label">이메일</label>
      <input
          v-model="loginForm.email"
          type="email"
          class="input-box"
          placeholder="이메일 입력"
          :disabled="isLoading"
      />

      <label class="label">비밀번호</label>
      <input
          v-model="loginForm.password"
          type="password"
          class="input-box"
          placeholder="비밀번호 입력"
          :disabled="isLoading"
          @keyup.enter="handleLogin"
      />

      <button
          class="login-button"
          :disabled="!isLoginFormValid || isLoading"
          @click="handleLogin"
      >
        {{ isLoading ? '로그인 중...' : '로그인' }}
      </button>
    </div>

    <!-- 회원가입 전환 섹션 -->
    <div class="signup-section">
      <div class="line left-line"></div>
      <div class="or-text">또는</div>
      <div class="line right-line"></div>

      <div class="no-account-text">계정이 없으신가요?</div>
      <div class="signup-text" @click="router.push('/agreement')">회원가입</div>
    </div>
  </div>
</template>

<style scoped>
/* 전체 컨테이너 스타일 */
.join-container {
  width: 384px;
  height: 800px;
  position: relative;
  background: #f8fafc;
  box-shadow: 0px 25px 50px -12px rgba(0, 0, 0, 0.25);
  overflow: auto;
  border-radius: 24px;
  outline: 1px black solid;
  outline-offset: -1px;
  margin: 0 auto;
  padding: 32px;
  box-sizing: border-box;
}

/* 로고 이미지 */
.logo {
  width: 250px;
  height: auto;
  display: block;
  margin: 0 auto 12px auto;
}

/* 서브 타이틀 */
.subtitle {
  text-align: center;
  font-size: 16px;
  color: #8d8d8d;
  margin-bottom: 24px;
}

/* 로그인 영역 */
.form-wrapper {
  width: 100%;
}

/* 회원가입 영역 */
.form-area {
  width: 100%;
  margin-top: 24px;
}

.label {
  font-size: 14px;
  font-weight: 500;
  color: #333;
  margin-top: 24px;
  display: block;
}

.password-label {
  margin-top: 20px;
}

.input-box {
  width: 100%;
  height: 52px;
  border-radius: 12px;
  border: 2px solid #e2e8f0;
  background: white;
  padding: 0 12px;
  font-size: 16px;
  margin-top: 6px;
  box-sizing: border-box;
  transition: border-color 0.3s ease;
}

.input-box:focus {
  outline: none;
  border-color: #fddf99;
}

.input-box:disabled {
  background-color: #f5f5f5;
  cursor: not-allowed;
}

.input-box.short {
  width: 229px;
  margin-right: 8px;
}

.nickname-wrapper {
  display: flex;
  align-items: center;
  gap: 8px;
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
  transition: background-color 0.3s ease;
}

.check-button:hover:not(:disabled) {
  background: #fcd34d;
}

.check-button:disabled {
  background: #e5e7eb;
  cursor: not-allowed;
}

.nickname-check {
  font-size: 12px;
  margin-top: 4px;
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
  line-height: 1.5;
}

.password-check .error,
.nickname-check .error,
.account-error {
  color: #a76a6a;
}

.password-check .success,
.nickname-check .success {
  color: #61a569;
}

.select-box {
  margin-top: 6px;
}

.dropdown {
  width: 100%;
  height: 52px;
  border-radius: 12px;
  border: 2px solid #e2e8f0;
  background: white;
  padding: 0 12px;
  font-size: 14px;
  cursor: pointer;
  transition: border-color 0.3s ease;
}

.dropdown:focus {
  outline: none;
  border-color: #fddf99;
}

.dropdown:disabled {
  background-color: #f5f5f5;
  cursor: not-allowed;
}

/* 계좌 정보 섹션 */
.account-section {
  margin-top: 32px;
  padding: 20px;
  background-color: #f9fafb;
  border-radius: 12px;
  border-left: 4px solid #fddf99;
}

.account-header {
  margin-bottom: 16px;
}

.account-header h4 {
  margin: 0 0 4px 0;
  font-size: 16px;
  font-weight: 600;
  color: #333;
}

.account-subtitle {
  font-size: 12px;
  color: #6b7280;
}

.account-error {
  font-size: 12px;
  margin-top: 4px;
}

/* TODO: Account 기능 완성 후 주석 해제 */
/* .existing-accounts {
  margin-top: 16px;
  padding-top: 16px;
  border-top: 1px solid #e5e7eb;
}

.existing-accounts h5 {
  margin: 0 0 12px 0;
  font-size: 14px;
  font-weight: 600;
  color: #374151;
}

.account-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 8px 12px;
  background: white;
  border-radius: 8px;
  margin-bottom: 8px;
  font-size: 12px;
}

.bank-name {
  font-weight: 600;
  color: #374151;
}

.account-number {
  color: #6b7280;
}

.balance {
  color: #059669;
  font-weight: 500;
} */

.login-button {
  width: 100%;
  height: 56px;
  background: #fddf99;
  border-radius: 12px;
  font-size: 16px;
  font-weight: 800;
  color: #2e363a;
  margin-top: 16px;
  cursor: pointer;
  border: none;
  transition: background-color 0.3s ease;
}

.login-button:hover:not(:disabled) {
  background: #fcd34d;
}

.login-button:disabled {
  background: #e5e7eb;
  cursor: not-allowed;
  color: #9ca3af;
}

.signup-btn {
  margin-top: 24px;
}

.form-notice {
  text-align: center;
  margin-top: 12px;
}

.required {
  font-size: 12px;
  color: #6b7280;
}

.signup-section {
  width: 320px;
  margin: 40px auto 0 auto;
  text-align: center;
  position: relative;
}

.line {
  width: 130px;
  height: 1px;
  border-top: 1px solid #cbd5e1;
  position: absolute;
  top: 10px;
}

.left-line {
  left: 0;
}

.right-line {
  right: 0;
}

.or-text {
  font-size: 14px;
  color: #8d8d8d;
  line-height: 20px;
  position: relative;
}

.no-account-text {
  margin-top: 40px;
  font-size: 14px;
  color: #8d8d8d;
  display: inline;
}

.signup-text {
  font-size: 14px;
  font-weight: 700;
  color: #333333;
  margin-left: 6px;
  display: inline;
  cursor: pointer;
  transition: color 0.3s ease;
}

.signup-text:hover {
  color: #fddf99;
}
</style>
