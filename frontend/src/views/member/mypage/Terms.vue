<script setup>
import { useRouter } from "vue-router";
import Header from '@/components/layout/Header.vue'

const router = useRouter()

// Header에서 이전 페이지로 이동
const goBack = () => {
  router.back()
}
</script>

<template>
  <div class="terms-wrapper">
    <Header title="이용 약관 동의" :back-action="goBack" />

    <div class="terms-list">
      <router-link to="/terms" class="terms-card">
        <h3>서비스 이용 약관</h3>
      </router-link>

      <router-link to="/privacy" class="terms-card">
        <h3>개인정보 수집 및 이용 동의</h3>
      </router-link>

      <router-link to="/push" class="terms-card">
        <h3>푸시 알림 동의</h3>
      </router-link >
    </div>
  </div>
</template>

<style scoped>
.terms-wrapper {
  width: 384px;
  margin: 0 auto;
  background: #f8f9fa;
  border-radius: 24px;
  box-shadow: 0px 25px 50px -12px rgba(0, 0, 0, 0.25);
  padding: 16px;
  height: 800px;
  overflow-y: auto;
  box-sizing: border-box;
}

.terms-list {
  display: flex;
  flex-direction: column;
  gap: 20px;
  margin-top: 20px;
}

.terms-card {
  display: block;
  padding: 20px;
  background: #fff;
  border-radius: 16px;
  text-decoration: none;
  color: #4a4a4a;
  font-family: 'Inter', sans-serif;
  font-weight: 700;
  font-size: 18px;
  box-shadow: 0px 4px 4px -3px rgba(0, 0, 0, 0.25);
}

.terms-card:hover {
  background-color: #f1f5f9;
}
</style>