<script setup>
import { ref, computed } from 'vue';
import { useRouter } from 'vue-router';
import { useAuthStore } from '@/stores/authStore';
import JoinPage from './JoinPage.vue';
import { requestPermissionAndGetToken } from "@/firebase.js";
import {loginMember} from "@/api/memberApi.js";

/**
 * N빵 트립 로그인 페이지
 * - 기본 로그인 기능
 * - FCM 토큰 발급 및 서버 저장 (백그라운드)
 * - 여행 그룹 서비스 즉시 이용 가능
 */
const router = useRouter();
const authStore = useAuthStore();

// 상태 관리
const isSignupMode = ref(false);
const isLoading = ref(false);

// 로그인 폼
const loginForm = ref({
  email: '',
  password: '',
});

// 폼 유효성 검사
const isLoginFormValid = computed(() =>
    loginForm.value.email && loginForm.value.password
);

/**
 * 메인 로그인 처리 함수
 */
const handleLogin = async () => {
  if (!isLoginFormValid.value) {
    alert('이메일과 비밀번호를 입력해주세요.');
    return;
  }

  try {
    isLoading.value = true;

    // 1. FCM 토큰 먼저 받기
    const fcmToken = await requestPermissionAndGetToken();

    // 2. 로그인 API 호출 (FCM 토큰 포함)
    const response = await loginMember({
      email: loginForm.value.email,
      password: loginForm.value.password,
      fcmToken: fcmToken
    });

    // 3. 인증 정보 저장
    authStore.setUser(response.userInfo);
    authStore.setToken(response.accessToken);

    // 4. localStorage에 토큰들 저장
    localStorage.setItem('accessToken', response.accessToken);

    // FCM 토큰 localStorage에 자동 저장
    if (fcmToken) {
      localStorage.setItem('fcmToken', fcmToken);
    }

    // 6. 메인 페이지로 이동
    await router.push('/');

  } catch (err) {
    console.error('❌ 로그인 오류:', err);

    // 구체적인 에러 메시지 제공
    let errorMessage = '로그인에 실패했습니다.';

    if (err.message.includes('FCM')) {
      errorMessage = '알림 설정 중 오류가 발생했지만 로그인은 계속 진행됩니다.';
      console.log('🔔 FCM 토큰 없이 로그인 재시도...');

      // FCM 없이 재시도
      try {
        const response = await loginMember({
          email: loginForm.value.email,
          password: loginForm.value.password
        });

        if (response.accessToken) {
          authStore.setToken(response.accessToken);
          authStore.setUser(response.userInfo);
          localStorage.setItem('accessToken', response.accessToken);
          await router.push('/');
          return;
        }
      } catch (retryErr) {
        console.error('❌ 재시도 실패:', retryErr);
        errorMessage = retryErr.message || '로그인에 실패했습니다.';
      }
    } else {
      errorMessage = err.message || '로그인에 실패했습니다.';
    }

    alert(errorMessage);

  } finally {
    isLoading.value = false;
  }
};

/**
 * 서버에 FCM 토큰 저장
 */
const saveFCMTokenToServer = async (fcmToken) => {
  try {
    const accessToken = localStorage.getItem('accessToken');
    if (!accessToken) return false;

    const response = await fetch('/api/users/fcm-token', {
      method: 'PUT',
      headers: {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${accessToken}`
      },
      body: JSON.stringify({ fcmToken })
    });

    if (response.ok) {
      localStorage.setItem('fcmToken', fcmToken);
      return true;
    } else {
      localStorage.setItem('fcmToken', fcmToken);
      return false;
    }
  } catch (error) {
    localStorage.setItem('fcmToken', fcmToken);
    return false;
  }
};
</script>

<template>
  <JoinPage v-if="isSignupMode" @signup-complete="isSignupMode = false" />

  <div v-else class="login-content">
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
.login-content {
  width: 100%;
  height: 100%; /* 부모(DefaultLayout)의 높이를 채우도록 변경 */
  overflow-y: auto; /* 내용이 길어지면 스크롤 */
  padding: 32px;
  box-sizing: border-box;
  display: flex;
  flex-direction: column;
  align-items: center;
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

.password-check .error,
.nickname-check .error,
.account-error {
  color: #a76a6a;
}

.password-check .success,
.nickname-check .success {
  color: #61a569;
}

.account-header h4 {
  margin: 0 0 4px 0;
  font-size: 16px;
  font-weight: 600;
  color: #333;
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