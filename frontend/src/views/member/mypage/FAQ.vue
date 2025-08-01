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
  <div class="faq-wrapper">
    <!-- 상단 헤더 -->
    <Header title="공지사항 및 FAQ" :back-action="goBack" />

    <!-- 이미지 및 메시지 -->
    <div class="content">
      <img src="@/assets/img/crying_cat.png" alt="송금 실패 이미지" class="cat-image" />
      <div class="message">아직 준비된 공지사항 및 FAQ가 없어요.</div>
    </div>

    <!-- 하단 버튼 -->
    <Button label="마이페이지로 돌아가기" @click="goBack" />
  </div>
</template>

<style scoped>
.faq-wrapper {
  width: 384px;
  height: 800px;
  margin: 0 auto;
  background: #f8f9fa;
  box-shadow: 0px 25px 50px -12px rgba(0, 0, 0, 0.25);
  border-radius: 24px;
  display: flex;
  flex-direction: column;
  position: relative;
  box-sizing: border-box;
  overflow: hidden;
}

/* 콘텐츠 영역 (중앙 정렬) */
.content {
  flex-grow: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center; /* 가운데 정렬 */
  padding: 0 16px;
  box-sizing: border-box;
}

.cat-image {
  width: 250px;
  height: auto;
  margin-bottom: 24px;
}

.message {
  color: #4a4a4a;
  font-size: 18px;
  font-weight: 700;
  text-align: center;
  line-height: 24px;
}
</style>