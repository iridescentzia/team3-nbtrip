<script setup>
import { useRouter } from "vue-router";
import Header from '@/components/layout/Header.vue'
import { ChevronRight } from 'lucide-vue-next';

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
  <div class="page-wrapper">
    <!-- 헤더 -->
    <Header title="이용 약관" :back-action="goBack" /><br>

    <!-- 약관 카드 리스트 -->
    <section class="form">
      <div
          v-for="(card, index) in cards"
          :key="index"
          class="terms-card"
          @click="router.push({ path: card.route, query: { viewOnly: true } })"
      >
        {{ card.title }}
        <ChevronRight class="arrow"/>
      </div>
    </section>
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
  padding-top: calc(56px + 24px); /* header height + 여백 */
  padding-left: 24px;
  padding-right: 24px;
  gap: 16px;
}

/* 약관 카드 스타일 */
.terms-card {
  background-color: white;
  border-radius: 1rem;
  padding: 1rem 1.25rem;
  box-shadow: 0 1px 3px rgba(0 0 0 / 0.1), 0 1px 2px -1px rgba(0 0 0 / 0.1);
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 16px;
  font-weight: 600;
  color: #333;
  cursor: pointer;
  transition: background-color 0.2s ease;
}

.terms-card:hover {
  background-color: #f1f5f9;
}

.arrow {
  font-size: 1.25rem;
  color: #bbb;
}
</style>