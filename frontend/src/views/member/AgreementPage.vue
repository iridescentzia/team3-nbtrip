<script setup>
// ... <script setup> 내용은 이전과 동일하게 유지 ...
import Header from '@/components/layout/Header.vue';
import Button from '@/components/common/Button.vue';
import { ref, watch } from 'vue';
import { useRouter } from 'vue-router';
import { useAgreementStore } from '@/stores/agreementStore.js';
import { ChevronRight } from 'lucide-vue-next';

const router = useRouter();
const agreementStore = useAgreementStore();

// 약관 항목
const agreements = ref([
  {
    id: 'terms',
    label: '이용 약관 동의 (필수)',
    route: '/agreement/terms',
    checked: false,
  },
  {
    id: 'privacy',
    label: '개인정보 수집·이용 동의 (필수)',
    route: '/agreement/privacy',
    checked: false,
  },
  {
    id: 'myData',
    label: '마이데이터 수집·활용 동의 (필수)',
    route: '/agreement/mydata',
    checked: false,
  },
  {
    id: 'push',
    label: '푸시 알림 수신 동의 (필수)',
    route: '/agreement/push',
    checked: false,
  },
]);

// store 값 -> agreements 반영
watch(
    () => agreementStore.agreements,
    (storeValue) => {
      agreements.value.forEach((a) => {
        a.checked = storeValue[a.id];
      });
    },
    { immediate: true, deep: true }
);

// agreements 값 -> store에 반영
watch(
    agreements,
    (localValue) => {
      localValue.forEach((a) => {
        agreementStore.agreements[a.id] = a.checked;
      });
    },
    { deep: true }
);

// 전체 동의 상태
const agreeAll = ref(false);
watch(
    agreements,
    () => {
      agreeAll.value = agreements.value.every((a) => a.checked);
    },
    { immediate: true, deep: true }
);

// 전체 동의 toggle
const toggleAll = () => {
  const value = !agreeAll.value;
  agreements.value.forEach((a) => {
    a.checked = value;
  });
};

// 필수 약관 모두 동의 여부 (실제 체크 여부 기준)
const allRequiredChecked = ref(false);
watch(
    agreements,
    () => {
      allRequiredChecked.value = agreements.value.every((a) => a.checked);
    },
    { immediate: true, deep: true }
);

// 상세 페이지 이동
const goToDetail = (route) => {
  router.push(route);
};

// 회원가입 페이지로 이동
const goToJoin = () => {
  if (!allRequiredChecked.value) {
    alert('필수 약관에 모두 동의해주세요.');
    return;
  }
  router.push({ path: '/join', query: { agreed: 'true' } });
};

// Header에서 이전 페이지로 이동
const goBack = () => {
  router.back();
};
</script>

<template>
  <div class="agreement-content">
    <Header title="약관 동의" :back-action="goBack" />

    <!-- 메인 콘텐츠 영역 -->
    <main class="main-content">
      <!-- 1. 상단 영역 -->
      <div class="top-section">
        <img src="@/assets/img/logo.png" alt="고양이 로고" class="logo" />
        <div class="subtitle">환영합니다!</div>
      </div>

      <!-- 2. 하단 영역 (약관 동의) -->
      <div class="bottom-section">
        <!-- 전체 동의 -->
        <div class="check-item all" @click="toggleAll">
          <input type="checkbox" id="agreeAll" :checked="agreeAll" />
          <label for="agreeAll">약관 전체 동의</label>
        </div>

        <!-- 개별 약관 목록 -->
        <div class="terms-list">
          <div v-for="item in agreements" :key="item.id" class="check-row">
            <div class="left" @click="item.checked = !item.checked">
              <input type="checkbox" :id="item.id" v-model="item.checked" />
              <label :for="item.id">{{ item.label }}</label>
            </div>
            <ChevronRight
                class="chevron-icon"
                @click="goToDetail(item.route)"
            />
          </div>
        </div>
      </div>
    </main>

    <!-- 하단 버튼 -->
    <footer class="bottom-fixed">
      <Button
          label="동의하고 회원가입 하기"
          :disabled="!allRequiredChecked"
          @click="goToJoin"
          class="agreement-button"
      />
    </footer>
  </div>
</template>

<style scoped>
/* 전체 컨테이너 */
.agreement-content {
  width: 100%;
  height: 100%;
  background: #f8fafc;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

/* 메인 콘텐츠 영역을 flex container로 설정 */
.main-content {
  flex: 1;
  overflow-y: auto;
  padding: 24px;
  padding-top: 80px;
  box-sizing: border-box;
  display: flex;
  flex-direction: column;
}

.top-section {
}

/* 하단 영역(약관)이 위로 남은 공간을 모두 차지하도록 설정 */
.bottom-section {
  margin-top: 80px;
  flex: 1;
  min-height: 0;
}

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

.check-item.all {
  display: flex;
  align-items: center;
  font-size: 16px;
  font-weight: 600;
  color: #2e2e2e;
  margin-bottom: 16px;
  cursor: pointer;
}

.terms-list {
  width: 100%;
  border-top: 1px solid #e2e8f0;
}

.check-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px 0;
  border-bottom: 1px solid #e2e8f0;
  font-size: 14px;
  color: #333;
}

.check-row:last-child {
  border-bottom: none;
}

.left {
  display: flex;
  align-items: center;
  flex: 1;
  cursor: pointer;
}

.left label {
  flex: 1;
  white-space: nowrap;
  cursor: pointer;
}

input[type='checkbox'] {
  appearance: none; /* 브라우저 기본 체크박스 스타일 제거 */
  -webkit-appearance: none;
  -moz-appearance: none;

  width: 20px;
  height: 20px;
  margin-right: 8px;
  border: 2px solid #ffd166;       /* 노란색 테두리 */
  border-radius: 4px;               /* 살짝 둥근 모서리 */
  background-color: white;          /* 기본 배경색 흰색 */
  cursor: pointer;
  position: relative;
  transition: background-color 0.2s, border-color 0.2s;
}

/* 체크된 상태 */
input[type='checkbox']:checked {
  background-color: #ffd166; /* 노란색 배경 */
}

/* 체크 표시(✓) */
input[type='checkbox']:checked::after {
  content: '';
  position: absolute;
  top: 50%;
  left: 50%;
  width: 5px;
  height: 9px;
  border: solid white;       /* 체크 표시 색상: 흰색 */
  border-width: 0 2px 2px 0;
  transform: translate(-50%, -50%) rotate(45deg);
}

.chevron-icon {
  width: 20px;
  height: 20px;
  color: #8d8d8d;
  flex-shrink: 0;
  cursor: pointer;
}

.bottom-fixed {
  padding: 16px 24px;
  background-color: #f8fafc;
}

.agreement-button {
  margin-bottom: 20px;
}

:deep(.custom-button) {
  width: 100%;
}
</style>