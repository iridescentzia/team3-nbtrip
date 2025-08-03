<script setup></script>

<template>
  <div class="view-wrapper">
    <div class="settlement-view">
      <RouterView />
    </div>
  </div>
</template>

<style scoped>
.settlement-view {
  --theme-primary: rgba(255, 209, 102, 0.65);
  --theme-secondary: rgba(162, 210, 255, 0.65);
  --theme-bg: #f8f9fa;
  --theme-text: #333333;
  --theme-text-light: #888888;
  --theme-red: #ef4444;
  --theme-blue: #3a86ff;
}

/* 화면 중앙 정렬을 위한 wrapper 스타일 */
.view-wrapper {
  display: flex;
  justify-content: center;
  /* align-items: center; <<-- 상단 정렬을 위해 주석 처리 */
  width: 100%;
  min-height: 100vh;
  background-color: var(--theme-bg); /* 하얀 배경 */
  padding: 2rem 0;
}

/* 스마트폰 모양의 전체 레이아웃 */
.settlement-view {
  width: 100%;
  max-width: 24rem; /* 384px */
  background-color: var(--theme-bg); /* Tailwind 'bg-slate-50' */
  display: flex;
  flex-direction: column;
  border-radius: 1.5rem; /* 24px */
  box-shadow: 0 25px 50px -12px rgb(0 0 0 / 0.25);
  overflow: hidden;
  position: relative;
  height: 844px; /* 스마트폰 높이 고정 */
  max-height: 90vh;
}
</style>
