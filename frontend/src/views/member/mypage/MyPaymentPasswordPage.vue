<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { verifyPassword } from "@/api/memberApi.js";

// 공통 컴포넌트
import Header from '@/components/layout/Header.vue'
import Button from '@/components/common/Button.vue'

// 상태 변수
const password = ref('');
const errorMessage = ref('');
const router = useRouter();

// Header에서 이전 페이지로 이동
const goBack = () => {
  router.back()
};

// 완료 버튼 클릭 시 실행되는 비밀번호 검증 함수
const handleVerifyPassword = async () => {
  try {
    const token = localStorage.getItem('accessToken')
    const response = await verifyPassword(password.value)

    // 비밀번호 검증 성공 시 /mypage/updatePayment 페이지로 이동
    if (response.success) {
      router.push('/mypage/updatePayment')
    } else {
      errorMessage.value = response.message || '비밀번호가 올바르지 않습니다.'
    }
  } catch (err) {
    errorMessage.value = err.message || '오류가 발생했습니다.'
    console.error(err)
  }
};
</script>

<template>
  <div class="page-wrapper">
    <!-- 상단 헤더 컴포넌트 -->
    <Header title="결제 수단 관리" :back-action="goBack"/>

    <!-- 안내 문구 -->
    <main class="main-content">
      <p class="guide-text">
        개인 정보 보호를 위해<br />
        비밀 번호를 입력해주세요.
      </p>

      <!-- 비밀번호 입력 -->
      <input
          type="password"
          v-model="password"
          placeholder="비밀번호 입력"
          class="password-input"
          @keyup.enter="handleVerifyPassword"
      />

      <!-- 에러 메시지 출력 -->
      <div v-if="errorMessage" class="error-message">{{ errorMessage }}</div>
    </main>

    <!-- 완료 버튼 -->
      <Button label="완료" @click="handleVerifyPassword" />
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

.main-content {
  flex: 1;
  display: flex;
  flex-direction: column;
  padding-top: calc(56px + 24px); /* Header 높이 */
  padding-left: 24px;
  padding-right: 24px;
}

/* 안내문 */
.guide-text {
  text-align: center;
  color: #4a4a4a;
  font-size: 20px;
  font-weight: 800;
  line-height: 1.5;
  margin-top: 160px;
  margin-bottom: 40px;
}

/* 입력창 */
.password-input {
  width: 100%; /* 부모 영역의 너비를 100% 채움 */
  height: 52px;
  background: #fff;
  border-radius: 12px;
  border: 2px solid #e2e8f0;
  padding: 0 16px;
  font-size: 16px;
  margin-bottom: 16px;
  box-sizing: border-box; /* 패딩과 테두리를 너비에 포함 */
}

/* 에러 메시지 */
.error-message {
  color: red;
  font-size: 14px;
  margin-bottom: 12px;
}
</style>