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
})

// 부모 컴포넌트로 전달할 클릭 이벤트
const emits = defineEmits(['click'])
</script>

<template>
  <button class="fixed bottom-[85px] left-1/2 -translate-x-1/2
                 w-[calc(100%-32px)] max-w-[343px] h-14
                 bg-[#ffd166a6] text-[#333] text-base font-semibold font-ibm
                 rounded-xl shadow-[0_2px_6px_rgba(0,0,0,0.05)]
                 cursor-pointer z-[200] transition-colors duration-200
                 hover:bg-[#ffd166] hover:-translate-y-px
                 active:translate-y-0
                 disabled:bg-[#e2e2e2] disabled:text-[#999] disabled:cursor-not-allowed"
          :disabled="disabled"
          @click="$emit('click')">
    {{ label }}
  </button>
</template>

<style scoped>
</style>
