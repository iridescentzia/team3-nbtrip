<script setup>
import { useRouter } from "vue-router";
import Header from '@/components/layout/Header.vue'

const router = useRouter()

// 이용 약관 카드
const cards = [
  {
    title: '서비스 이용 약관',
    route: '/agreement/terms'
  },
  {
    title: '개인정보 수집 및 이용 동의',
    route: '/agreement/privacy'
  },
  {
    title: '푸시 알림 수신 동의',
    route: '/agreement/push'
  }
]

// Header에서 뒤로가기
const goBack = () => {
  router.back()
}
</script>

<template>
  <div class="terms-wrapper">
    <!-- 헤더 -->
    <Header title="이용 약관" :back-action="goBack" /><br>

    <!-- 약관 카드 리스트 -->
    <section class="section">
      <div
          v-for="(card, index) in cards"
          :key="index"
          class="terms-card"
          @click="router.push({ path: card.route, query: { viewOnly: true } })"
      >
        {{ card.title }}
        <span class="arrow">›</span>
      </div>
    </section>
  </div>
</template>

<style scoped>
.terms-wrapper {
  --theme-bg: #f8f9fa;
  --theme-text: #333333;
  --theme-text-light: #888888;

  width: 100%;
  max-width: 24rem; /* 384px */
  height: 844px;
  margin: 0 auto;
  background-color: var(--theme-bg);
  border-radius: 1.5rem;
  box-shadow: 0 25px 50px -12px rgba(0, 0, 0, 0.25);
  padding: 1.25rem;
  box-sizing: border-box;
  overflow-y: auto;
}

/* 카드 리스트 구역 */
.section {
  display: flex;
  flex-direction: column;
  gap: 1.25rem; /* 20px */
  margin-top: 1.5rem;
}

/* 개별 약관 카드 */
.terms-card {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 1.25rem 1.5rem;
  background-color: white;
  border-radius: 1rem;
  font-size: 1rem;
  font-weight: 700;
  color: var(--theme-text);
  box-shadow: 0 1px 3px rgba(0 0 0 / 0.1), 0 1px 2px -1px rgba(0 0 0 / 0.1);
  cursor: pointer;
  transition: background-color 0.2s ease;
}

.terms-card:hover {
  background-color: #f1f5f9;
}

.arrow {
  font-size: 1.25rem;
  color: #bbbbbb;
}
</style>