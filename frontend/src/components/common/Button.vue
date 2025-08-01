<script setup>
defineProps({
  // 버튼에 표시될 텍스트
  label: {
    type: String,
    default: '버튼',
  },

  // 버튼 비활성화 여부(기본값 : 활성화 상태)
  disabled: {
    type: Boolean,
    default: false,
  },

  // 버튼 타입(버튼 타입에 맞게 지정)
  type: {
    type: String,
    default: 'button',
  },
});

// 부모 컴포넌트로 전달할 클릭 이벤트
const emits = defineEmits(['click']);
</script>

<template>
  <button class="bottom-button" :disabled="disabled" @click="$emit('click')">
    {{ label }}
  </button>
</template>

<style scoped>
/* 기본 버튼 스타일 */
.bottom-button {
  display: block;
  margin: auto auto 20px auto;

  width: calc(100% - 32px);
  max-width: 343px;
  height: 56px;
  background-color: rgba(255, 209, 102, 0.65);
  color: #333;
  font-size: 16px;
  font-weight: 600;
  font-family: 'IBM Plex Sans KR', sans-serif;
  border: none;
  border-radius: 12px;
  box-shadow: 0 2px 6px rgba(0, 0, 0, 0.05);
  cursor: pointer;
  z-index: 1001;
  transition: background-color 0.2s;
}

.bottom-button:hover:not(:disabled) {
  background-color: rgba(255, 209, 102, 1);
  /* 위치 이동 제거 */
}

.bottom-button:active:not(:disabled) {
  background-color: rgba(255, 209, 102, 1);
  /* 위치 이동 제거 */
}

.bottom-button:disabled {
  background-color: #e2e2e2;
  color: #999;
  cursor: not-allowed;
}
</style>
