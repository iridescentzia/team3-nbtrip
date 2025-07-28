<script setup>
import { ref, computed } from 'vue'

const agreeAll = ref(false)
const agreements = ref([
  { id: 'terms', label: '이용 약관 동의 (필수)', checked: false },
  { id: 'privacy', label: '개인정보 이용 및 수집 동의 (필수)', checked: false },
  { id: 'push', label: '푸시 알림 발송 동의 (필수)', checked: false },
])

const toggleAll = () => {
  agreements.value.forEach(a => (a.checked = agreeAll.value))
}

const allChecked = computed(() => agreements.value.every(a => a.checked))
</script>

<template>
  <div class="agreement-container">
    <!-- 로고 및 타이틀 -->
    <img src="https://placehold.co/250x250" alt="고양이 로고" class="logo" />
    <div class="title">N빵트립</div>
    <div class="subtitle">환영합니다!</div>

    <!-- 전체 동의 -->
    <div class="check-item">
      <input type="checkbox" id="agreeAll" v-model="agreeAll" @change="toggleAll" />
      <label for="agreeAll">약관 전체 동의</label>
    </div>

    <!-- 개별 약관 항목 -->
    <div v-for="item in agreements" :key="item.id" class="check-item">
      <input type="checkbox" :id="item.id" v-model="item.checked" />
      <label :for="item.id">{{ item.label }}</label>
      <span class="arrow">›</span>
    </div>

    <!-- 회원가입 버튼 -->
    <button class="agree-btn" :disabled="!allChecked">동의하고 회원가입 하기</button>
  </div>
</template>

<style scoped>
.agreement-container {
  width: 384px;
  height: 800px;
  background: #f8fafc;
  border-radius: 24px;
  outline: 1px solid black;
  outline-offset: -1px;
  box-shadow: 0 25px 50px -12px rgba(0, 0, 0, 0.25);
  padding: 32px 24px;
  box-sizing: border-box;
  position: relative;
  overflow: hidden;
}

.logo {
  width: 250px;
  display: block;
  margin: 0 auto;
}

.title {
  font-family: 'Black Han Sans', sans-serif;
  font-size: 32px;
  color: #2f2d2d;
  text-align: center;
  margin-top: 8px;
}

.subtitle {
  font-size: 24px;
  font-family: Inter, sans-serif;
  text-align: center;
  color: #000;
  margin-bottom: 32px;
}

.check-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  font-size: 16px;
  margin: 16px 0;
  font-family: Inter, sans-serif;
  color: #8d8d8d;
}

.check-item input[type='checkbox'] {
  margin-right: 8px;
  width: 20px;
  height: 20px;
}

.check-item .arrow {
  font-size: 24px;
  color: #8d8d8d;
}

.agree-btn {
  width: 100%;
  height: 56px;
  background: #fddf99;
  border: none;
  border-radius: 12px;
  font-size: 16px;
  font-weight: 800;
  color: #2e363a;
  font-family: Inter, sans-serif;
  margin-top: 32px;
  cursor: pointer;
}

.agree-btn:disabled {
  background: #e2e2e2;
  color: #aaa;
  cursor: not-allowed;
}
</style>