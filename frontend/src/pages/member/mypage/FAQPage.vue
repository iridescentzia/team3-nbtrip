<script setup>
import { useRouter } from 'vue-router'
import Header from '@/components/layout/Header.vue'
import Button from '@/components/common/Button.vue'

const router = useRouter()

// Header에서 뒤로가기
const goBack = () => {
  router.back()
}
</script>

<template>
  <div class="faq-page">
    <Header title="공지사항 및 FAQ" :back-action="goBack" />

    <div class="faq-content">
      <img src="@/assets/img/crying_cat.png" alt="송금 실패 이미지" class="cat-image" />
      <p class="message">아직 준비된 공지사항 및 FAQ가 없어요.</p>
    </div>

    <!-- 하단 버튼 -->
    <div class="bottom-button">
      <Button label="마이페이지로 돌아가기" @click="goBack" />
    </div>
  </div>
</template>

<style scoped>
.faq-page {
  display: flex;
  flex-direction: column;
  height: 100%;
  padding: 0 1.5rem;
  box-sizing: border-box;
}

.faq-content {
  flex: 1;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  text-align: center;
}

.cat-image {
  width: 240px;
  margin-bottom: 1.5rem;
}

.message {
  font-size: 18px;
  font-weight: 700;
  color: var(--theme-text);
  line-height: 1.4;
}

/* 하단 버튼 정렬 */
.bottom-button {
  margin-bottom: 1rem;
}
</style>