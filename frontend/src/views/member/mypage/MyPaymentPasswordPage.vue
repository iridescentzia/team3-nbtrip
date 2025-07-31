<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { verifyPassword } from "@/api/memberApi.js";

// 공통 컴포넌트
import Header from '@/components/layout/Header.vue'
import Button from '@/components/common/Button.vue'

// 상태 변수
const password = ref('')
const errorMessage = ref('')
const router = useRouter()

// Header에서 이전 페이지로 이동
const goBack = () => {
  router.back()
}

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
}
</script>

<template>
  <div class="page-wrapper">
    <!-- 상단 헤더 컴포넌트 -->
    <Header title="결제 수단 관리" :back-action="goBack"/>

    <!-- 안내 문구 -->
    <p class="guide-text">
      개인 정보 보호를 위해<br />
      비밀 번호를 입력해주세요.
    </p>

    <!-- 비밀번호 입력 필드 -->
    <input
        type="password"
        v-model="password"
        placeholder="비밀번호 입력"
        class="password-input"
    />

    <!-- 에러 메시지 출력 -->
    <div v-if="errorMessage" class="error-message">{{ errorMessage }}</div>

    <!-- 완료 버튼 -->
    <Button label="완료" @click="handleVerifyPassword" />
  </div>
</template>

<style scoped>
/* 전체 박스 스타일 */
.page-wrapper {
  width: 384px;
  height: 800px;
  background: #f8fafc;
  box-shadow: 0 25px 50px -12px rgba(0, 0, 0, 0.25);
  overflow: hidden;
  border-radius: 24px;
  outline: 1px solid black;
  outline-offset: -1px;
  margin: 0 auto;
  padding: 32px 24px;
  box-sizing: border-box;
  display: flex;
  flex-direction: column;
  align-items: stretch;
}

/* 안내 텍스트 */
.guide-text {
  text-align: center;
  color: #4a4a4a;
  font-size: 20px;
  font-weight: 800;
  line-height: 1.5;
  margin-top: 160px;
  margin-bottom: 40px;
}

/* 비밀번호 입력 박스 */
.password-input {
  width: 320px;
  height: 52px;
  background: #fff;
  border-radius: 12px;
  border: 2px solid #e2e8f0;
  padding: 0 16px;
  font-size: 16px;
  margin-bottom: 16px;
}

/* 에러 메시지 스타일 */
.error-message {
  color: red;
  font-size: 14px;
  margin-bottom: 12px;
}
</style>