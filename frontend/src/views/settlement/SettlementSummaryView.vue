<script setup>
import { ref, onMounted } from 'vue';
// 1. Header.vue 컴포넌트를 import 합니다.
import Header from '../../components/layout/Header.vue';
// 2. 실제 API 호출 함수와 라우터를 import 합니다.
import { getSettlementSummary, requestSettlement } from '@/api/settlementApi';
import { useRoute, useRouter } from 'vue-router';

const summaryData = ref(null);
const isLoading = ref(true);
const error = ref(null);

// 3. 실제 Vue Router를 사용하도록 설정합니다.
const route = useRoute();
const router = useRouter();
// 4. URL 파라미터에서 tripId를 동적으로 가져옵니다. (예: /settlement/1)
const tripId = route.params.tripId;

onMounted(async () => {
  // 최소 1초의 로딩 시간을 보장하기 위한 Promise
  const minLoadingTime = new Promise((resolve) => setTimeout(resolve, 800));

  try {
    // --- 수정된 부분 시작 ---
    // API 호출과 최소 로딩 시간을 동시에 실행하고 모두 끝나기를 기다림
    const [response] = await Promise.all([
      getSettlementSummary(tripId),
      minLoadingTime,
    ]);
    summaryData.value = response.data;
    // --- 수정된 부분 끝 ---
  } catch (err) {
    console.error('정산 요약 정보 로딩 실패:', err);

    if(err.response?.status === 403) {
      error.value = '그룹장만 정산을 요청할 수 있습니다.';

      // 3초 후 여행 상세 페이지로 리다이렉트
      setTimeout(() => {
        router.push(`/trip/${tripId}`);
      }, 3000);
    } else if (err.response?.status === 404) {
      error.value = '여행 정보를 찾을 수 없습니다.';
    } else {
      error.value = '데이터를 불러오는 데 실패했습니다.';
    }
  } finally {
    // try/catch 블록이 모두 끝난 후에 로딩 상태를 해제
    isLoading.value = false;
  }
});

const goToNextStep = async () => {
  if (!confirm('정산을 생성하시겠습니까?')) return;

  try {
    // 정산 생성 + n빵 계산 실행
    const response = await requestSettlement({ tripId });

    if (response.data.success) {
      alert('정산이 성공적으로 생성되었습니다!');
      // 다음 페이지로 이동
      router.push(`/settlement/${tripId}/request`);
    } else {
      alert(response.data.message);
    }
  } catch (err) {
    console.error('정산 생성 실패:', err);
    alert('정산 생성에 실패했습니다.');
  }
};

</script>

<template>
  <!-- 화면 중앙 정렬을 위한 wrapper -->
  <div class="view-wrapper">
    <div class="settlement-view">
      <!-- Header.vue 컴포넌트 사용 -->
      <Header title="정산하기" />

      <main v-if="isLoading" class="content-container loading">
        <div class="spinner"></div>
        <p>데이터를 불러오는 중...</p>
      </main>

      <main v-else-if="error" class="content-container error">
        <p>{{ error }}</p>
      </main>

      <main v-else-if="summaryData" class="content-container">
        <div class="summary-header">
          <p class="trip-name">{{ summaryData.tripName }}</p>
          <h2 class="total-amount">
            총 {{ summaryData.totalAmount?.toLocaleString() || 0 }}원 사용
          </h2>
        </div>

        <div class="member-payment-card">
          <h3 class="card-title">멤버별 결제 내역</h3>
          <div class="member-list">
            <div
              v-for="member in summaryData.memberPayments"
              :key="member.nickname"
              class="member-item"
            >
              <div class="member-info">
                <div class="member-avatar">
                  <span>{{ member.nickname.substring(0, 1) }}</span>
                </div>
                <span class="member-name">{{ member.nickname }}</span>
              </div>
              <p class="member-amount">
                {{ member.amount?.toLocaleString() || 0 }}원
              </p>
            </div>
          </div>
        </div>
      </main>

      <footer class="footer">
        <button @click="goToNextStep" class="next-button">다음</button>
      </footer>
    </div>
  </div>
</template>

<style scoped>
/* 테마 색상 변수 */
.settlement-view {
  --theme-primary: rgba(255, 209, 102, 0.65);
  --theme-primary-dark: #e2c05e;
  --theme-bg: #f8f9fa;
  --theme-text: #333333;
  --theme-text-light: #888888;
}

/* 화면 중앙 정렬을 위한 wrapper 스타일 */
.view-wrapper {
  display: flex;
  justify-content: center;
  /* align-items: center; */
  width: 100%;
  min-height: 100vh; /* 화면 전체 높이를 차지하도록 */
  background-color: #ffffff;
  padding: 2rem 0; /* 위아래 여백 추가 */
}

/* 전체 레이아웃 */
.settlement-view {
  z-index: 1;
  width: 100%;
  max-width: 24rem; /* 384px */
  background-color: var(--theme-bg);
  display: flex;
  flex-direction: column;
  border-radius: 1.5rem; /* 둥근 모서리 */
  box-shadow: 0 25px 50px -12px rgb(0 0 0 / 0.25); /* 그림자 효과 */
  overflow: hidden; /* 둥근 모서리 적용을 위해 */
  position: relative; /* Header 컴포넌트의 fixed 포지션 기준점 */
  height: 844px; /* 특정 스마트폰 높이를 기준으로 고정 */
  max-height: 90vh; /* 화면 높이의 90%를 넘지 않도록 설정 */
}

/* 메인 콘텐츠 */
.content-container {
  flex-grow: 1;
  padding: 1.25rem;
  overflow-y: auto;
  /* 헤더 높이(56px)만큼 상단에 여백을 주어 내용이 겹치지 않게 함 */
  padding-top: calc(56px + 1.25rem);
}

.loading,
.error {
  /* --- 이 부분을 수정 --- */
  display: flex;
  align-items: center;
  justify-content: center;
  text-align: center;
  color: var(--theme-text-light);
  padding-top: 56px; /* 헤더 높이만큼 여백 유지 */
  flex-grow: 1;
}

.loading {
  flex-direction: column; /* 아이템을 세로로 정렬 */
  gap: 1rem; /* 스피너와 텍스트 사이 간격 */
}

.spinner {
  width: 40px;
  height: 40px;
  border: 4px solid rgba(0, 0, 0, 0.1);
  border-left-color: var(--theme-primary);
  border-radius: 50%;
  animation: spin 1s linear infinite;
}

@keyframes spin {
  to {
    transform: rotate(360deg);
  }
}

.error p {
  color: #ef4444;
}

.summary-header {
  text-align: left;
  margin-bottom: 1.5rem;
}
.trip-name {
  color: var(--theme-text-light);
}
.total-amount {
  font-size: 1.875rem;
  font-weight: 800;
  color: var(--theme-text);
  margin-top: 0.25rem;
}

/* 멤버별 결제 내역 카드 */
.member-payment-card {
  background-color: white;
  border-radius: 1rem;
  padding: 1.25rem;
  box-shadow: 0 1px 3px 0 rgb(0 0 0 / 0.1), 0 1px 2px -1px rgb(0 0 0 / 0.1);
}
.card-title {
  font-weight: 700;
  color: var(--theme-text);
  padding-left: 0.25rem;
  margin: 5px 0 20px 0;
}
.member-list {
  margin-top: 1rem;
  display: flex;
  flex-direction: column;
}
.member-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.member-info {
  display: flex;
  align-items: center;
}
.member-avatar {
  width: 2.5rem;
  height: 2.5rem;
  border-radius: 9999px;
  margin-right: 0.75rem;
  background-color: var(--theme-primary);
  display: flex;
  align-items: center;
  justify-content: center;
}
.member-avatar span {
  font-weight: 700;
  font-size: 1.125rem;
  color: white;
}
.member-name {
  font-weight: 600;
  color: var(--theme-text);
}
.member-amount {
  font-weight: 700;
  font-size: 1.125rem;
  color: var(--theme-text);
}

/* 하단 버튼 */
.footer {
  padding: 1rem;
  background-color: var(--theme-bg);
  margin-top: auto;
  border-top: none;
}
.next-button {
  width: 100%;
  background-color: var(--theme-primary);
  color: var(--theme-text);
  font-weight: 800;
  padding: 1rem 0;
  border-radius: 0.75rem;
  transition: opacity 0.2s;
  border: none;
  cursor: pointer;
}
.next-button:hover {
  opacity: 0.9;
}
</style>
