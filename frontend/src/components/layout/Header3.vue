<script setup>
import { Home, Info } from 'lucide-vue-next';
import {useRouter} from "vue-router";

const props = defineProps({
  // 헤더 제목
  title: {
    type: String,
    default: '',
  },
  showInfo: {
    type: Boolean,
    default: false,
  }
});

const emit = defineEmits(['back']);

const router = useRouter();

function handleHomeClick() {
  router.push('/');
}

function handleInfoClick() {
  router.push('/settlement/explain');
}

</script>

<template>
  <header class="header">
    <!-- 왼쪽 영역  -->
    <div class="left">

    </div>

    <!-- 중앙 영역 : 제목 + 인포 아이콘 -->
    <div class="center">
      <div class="title-wrapper">
        <h1 v-if="title">{{ title }}</h1>
        <button
            v-if="showInfo"
            class="info-button-wrapper"
            @click="handleInfoClick"
            title="정산 과정 안내"
        >
          <Info class="info-button" />
        </button>
      </div>
    </div>

    <!-- 오른쪽 영역 (비어 있음) -->
    <div class="right">
      <button
          class="home-button-wrapper"
          @click="handleHomeClick"
      >
        <Home class="home-button" />
      </button>
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

.home-button-wrapper {
  padding: 8px;
  border-radius: 9999px;
  transition: background-color 0.2s ease, transform 0.1s ease;
  cursor: pointer;
  border: none;
  background: none;
}

.home-button-wrapper:hover {
  background-color: #f3f4f6; /* bg-gray-100 */
}

.home-button-wrapper:active {
  background-color: #e5e7eb; /* bg-gray-200 */
  transform: scale(0.95);
}

.home-button {
  width: 24px;
  height: 24px;
  color: #374151; /* text-gray-700 */
}

.title-wrapper {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 0.5rem;
}

.info-button-wrapper {
  padding: 4px;
  border-radius: 9999px;
  transition: background-color 0.2s ease, transform 0.1s ease;
  cursor: pointer;
  border: none;
  background: none;
  display: flex;
  align-items: center;
  justify-content: center;
}

.info-button-wrapper:hover {
  background-color: #f3f4f6; /* bg-gray-100 */
}

.info-button-wrapper:active {
  background-color: #e5e7eb; /* bg-gray-200 */
  transform: scale(0.95);
}

.info-button {
  width: 18px;
  height: 18px;
  color: #374151; /* text-gray-500 */
}

</style>