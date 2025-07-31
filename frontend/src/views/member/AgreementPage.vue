<script setup>
import Header from "@/components/layout/Header.vue";
import Button from "@/components/common/Button.vue";
import { ref, watch } from 'vue'
import { useRouter } from "vue-router";
import {useAgreementStore} from "@/stores/agreement.js";
import { ChevronRight } from 'lucide-vue-next';
import DefaultLayout from "@/components/layout/DefaultLayout.vue";

const router = useRouter()
const agreementStore = useAgreementStore()

// 약관 항목
const agreements = ref([
  { id: 'terms', label: '이용 약관 동의 (필수)', route: '/agreement/terms', checked: false },
  { id: 'privacy', label: '개인정보 수집 및 이용 동의 (필수)', route: '/agreement/privacy', checked: false },
  { id: 'push', label: '푸시 알림 수신 동의 (필수)', route: '/agreement/push', checked: false }
])

// store 값 -> agreements 반영
watch(
    () => agreementStore.agreements,
    (storeValue) => {
      agreements.value.forEach((a) => {
        a.checked = storeValue[a.id]
      })
    },
    { immediate: true, deep: true }
)

// agreements 값 -> store에 반영
watch(
    agreements,
    (localValue) => {
      localValue.forEach((a) => {
        agreementStore.agreements[a.id] = a.checked
      })
    },
    { deep: true }
)

// 전체 동의 상태
const agreeAll = ref(false)
watch(agreements, () => {
  agreeAll.value = agreements.value.every(a => a.checked)
}, { immediate: true, deep: true })

// 전체 동의 toggle
const toggleAll = () => {
  const value = agreeAll.value
  agreements.value.forEach((a) => {
    a.checked = value
  })
}

// 필수 약관 모두 동의 여부 (실제 체크 여부 기준)
const allRequiredChecked = ref(false)
watch(agreements, () => {
  allRequiredChecked.value = agreements.value.every(a => a.checked)
}, { immediate: true, deep: true })

// 상세 페이지 이동
const goToDetail = (route) => {
  router.push(route)
}

// 회원가입 페이지로 이동
const goToJoin = () => {
  if (!allRequiredChecked.value) {
    alert('필수 약관에 모두 동의해주세요.')
    return
  }
  router.push({ path: '/join', query: { agreed: 'true' } })
}

// Header에서 이전 페이지로 이동
const goBack = () => {
  router.back()
}

// "동의합니다" 클릭 시 store에 체크하고 뒤로가기
const handleAgree = () => {
  agreementStore.check('terms')
  goBack()
}
</script>

<template>
  <div class="agreement-container"><br/>
    <Header title="약관 동의" :back-action="goBack" />

    <!-- 로고 -->
    <img src="@/assets/img/logo.png" alt="고양이 로고" class="logo" />
    <div class="subtitle">환영합니다!</div>

    <!-- 전체 동의 -->
    <div class="check-item all">
      <input type="checkbox" id="agreeAll" v-model="agreeAll" @change="toggleAll" />
      <label for="agreeAll">약관 전체 동의</label>
    </div>

    <!-- 개별 약관 목록 -->
    <div class="terms-list">
      <div
          v-for="item in agreements"
          :key="item.id"
          class="check-row"
          @click="goToDetail(item.route)"
      >
        <div class="left">
          <input type="checkbox" :id="item.id" v-model="item.checked" @click.stop />
          <label :for="item.id">{{ item.label }}</label>
        </div>
        <ChevronRight class="chevron-icon" />
      </div>
    </div>


    <!-- 하단 버튼 -->
    <Button label="동의하고 회원가입 하기" :disabled="!allRequiredChecked" @click="goToJoin" />
  </div>
</template>

<style scoped>
.agreement-container {
  width: 384px;
  height: 800px;
  background: #f8fafc;
  box-shadow: 0 25px 50px -12px rgba(0, 0, 0, 0.25);
  overflow: hidden;
  border-radius: 24px;
  outline: 1px solid black;
  outline-offset: -1px;
  margin: 0 auto;
  padding: 32px 24px;
  box-sizing: border-box;
  display: flex;
  flex-direction: column;
  align-items: stretch;
}

/* 로고, 텍스트만 중앙 정렬 유지 */
.logo {
  width: 200px;
  display: block;
  margin: 0 auto 12px auto;
}

.subtitle {
  text-align: center;
  font-size: 18px;
  font-weight: 600;
  margin-bottom: 24px;
}

/* 전체 동의 항목 */
.check-item.all {
  display: flex;
  align-items: center;
  font-size: 16px;
  font-weight: 600;
  color: #2e2e2e;
  margin-bottom: 16px;
}

/* 전체 약관 리스트 */
.terms-list {
  width: 100%;
  border-top: 1px solid #e2e8f0;
  border-bottom: 1px solid #e2e8f0;
}

/* 개별 항목 행 정렬 */
.check-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px 0;
  border-bottom: 1px solid #e2e8f0;
  font-size: 14px;
  color: #333;
  cursor: pointer;
}

.check-row:last-child {
  border-bottom: none;
}

.left {
  display: flex;
  align-items: center;
  flex: 1;
}

.left label {
  flex: 1;
  white-space: nowrap;
}

input[type='checkbox'] {
  width: 20px;
  height: 20px;
  margin-right: 8px;
}

.chevron-icon {
  width: 20px;
  height: 20px;
  color: #8d8d8d;
  flex-shrink: 0;
}

/* 버튼 하단 고정 */
button {
  margin-top: 24px;
  width: 100%;
}
</style>