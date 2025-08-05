<script setup>
import { ChevronLeft, Home } from 'lucide-vue-next';

const props = defineProps({
  // 헤더 제목
  title: {
    type: String,
    default: '',
  },

  // 뒤로가기 버튼 표시 여부(기본: 표시)
  showBackButton: {
    type: Boolean,
    default: true,
  },

  // 뒤로가기 버튼 실행 시 액션(기본: 브라우저 뒤로가기)
  backAction: {
    type: Function,
    default: null,
  },
});

const emit = defineEmits(['back']);

const handleBackClick = () => {
  console.log('🔙 뒤로가기 클릭됨');
  if (props.backAction) {
    props.backAction();
  } else {
    emit('back');
  }
};
</script>

<template>
  <header class="header">
    <!-- 왼쪽 영역 : 뒤로가기 버튼 -->
    <div class="left">
      <button
        v-if="showBackButton"
        class="back-button-wrapper"
        @click="handleBackClick"
      >
        <ChevronLeft class="back-button" />
      </button>
    </div>

    <!-- 중앙 영역 : 제목 -->
    <div class="center">
      <h1 v-if="title">{{ title }}</h1>
    </div>

    <!-- 오른쪽 영역 (비어 있음) -->
    <div class="right">
      <!-- <button
        v-if="showBackButton"
        class="home-button-wrapper"
        @click="handleHomeClick"
      >
        <Home class="home-button" />
      </button> -->
    </div>
  </header>
</template>

<style scoped>
.header {
  position: absolute;
  top: 0;
  left: 50%;
  transform: translateX(-50%);
  z-index: 1000;

  display: flex;
  align-items: center;
  width: 100%;
  /* max-width: 375px; */
  max-width: 24em;

  height: 56px; /* h-14 */
  padding: 0 16px;
  background-color: #f9fafb;
  border-bottom: none; /* slate-200 */
  box-sizing: border-box;
}

.left,
.right {
  display: flex;
  align-items: center;
  flex: 1;
}

.left {
  justify-content: flex-start;
}

.right {
  justify-content: flex-end;
}

.center {
  text-align: center;
  flex-shrink: 0; /* 제목이 줄지 않게 고정 */
}

.center h1 {
  font-size: 1.125rem; /* text-lg */
  font-weight: 800; /* font-extrabold */
  color: #1f2937; /* text-gray-800 */
  white-space: nowrap;
  margin: 0;
}

.back-button-wrapper {
  padding: 8px;
  border-radius: 9999px;
  transition: background-color 0.2s ease, transform 0.1s ease;
  cursor: pointer;
  border: none;
  background: none;
}

.back-button-wrapper:hover {
  background-color: #f3f4f6; /* bg-gray-100 */
}

.back-button-wrapper:active {
  background-color: #e5e7eb; /* bg-gray-200 */
  transform: scale(0.95);
}

.back-button {
  width: 24px;
  height: 24px;
  color: #374151; /* text-gray-700 */
}
</style>
