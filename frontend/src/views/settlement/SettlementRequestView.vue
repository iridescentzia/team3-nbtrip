<script setup>
import { ref, onMounted, computed } from 'vue';
import Header from '../../components/layout/Header.vue';
import {
  getSettlementsByTripId,
  getSettlementSummary,
  requestSettlement,
} from '@/api/settlementApi';
import { useRoute, useRouter } from 'vue-router';

// --- 상태 관리 ---
const settlementData = ref(null);
const isLoading = ref(true);
const error = ref(null);
const selectedMember = ref(null); // 드롭다운에서 선택된 멤버

// --- 라우터 ---
const route = useRoute();
const router = useRouter();
const tripId = route.params.tripId;

// --- 데이터 로딩 ---
onMounted(async () => {
  const minLoadingTime = new Promise((resolve) => setTimeout(resolve, 700));

  try {
    const [settlementsResponse, summaryResponse] = await Promise.all([
      getSettlementsByTripId(tripId),
      getSettlementSummary(tripId),
      minLoadingTime,
    ]);

    // ✅ 안전한 데이터 처리
    const settlements = settlementsResponse.data || [];  // null 방어
    const summary = summaryResponse.data;

    // ✅ 빈 배열 체크 추가
    if (!Array.isArray(settlements)) {
      console.error('settlements is not an array:', settlements);
      error.value = '정산 데이터 형식이 올바르지 않습니다.';
      return;
    }

    if (settlements.length === 0) {
      error.value = '아직 정산이 생성되지 않았습니다. 정산을 먼저 생성해주세요.';
      return;
    }

    // 멤버 목록 추출
    const membersSet = new Set();
    settlements.forEach(s => {
      if (s.senderNickname) membersSet.add(s.senderNickname);
      if (s.receiverNickname) membersSet.add(s.receiverNickname);
    });

    // 나머지 코드...
    settlementData.value = {
      settlements: settlements,
      members: Array.from(membersSet),
      tripName: summary.tripName,
      totalAmount: summary.totalAmount,
      transactions: settlements
    };

    if (Array.from(membersSet).length > 0) {
      selectedMember.value = Array.from(membersSet)[0];
    }

  } catch (err) {
    console.error('정산 정보 로딩 실패:', err);
    console.error('에러 상세:', err.response?.data);  // ✅ 디버깅 추가

    if (err.response?.status === 403) {
      error.value = '그룹장만 정산을 조회할 수 있습니다.';
    } else {
      error.value = '데이터를 불러오는 데 실패했습니다.';
    }
  } finally {
    isLoading.value = false;
  }
});


// --- 계산된 속성 (Computed Properties) ---
// 기존 computed 속성들을 교체
const toReceiveList = computed(() => {
  if (!settlementData.value?.settlements) return [];
  return settlementData.value.settlements.filter(
      (tx) => tx.receiverNickname === selectedMember.value
  );
});

const toSendList = computed(() => {
  if (!settlementData.value?.settlements) return [];
  return settlementData.value.settlements.filter(
      (tx) => tx.senderNickname === selectedMember.value
  );
});

// 정산 요청 보내기 함수
const handleRequestSettlement = async () => {
  if (!confirm('모든 멤버에게 정산 요청을 보낼까요?')) return;

  try {
    await requestSettlement({ tripId });
    alert('정산 요청을 성공적으로 보냈습니다!');
    router.push('/'); // 홈으로 이동
  } catch (err) {
    console.error('정산 요청 실패:', err);
    alert('정산 요청에 실패했습니다.');
  }
};
</script>

<template>
  <div class="view-wrapper">
    <div class="settlement-view">
      <Header title="정산하기" />

      <main v-if="isLoading" class="content-container loading">
        <p>최종 정산 결과를 계산하는 중...</p>
      </main>
      <main v-else-if="error" class="content-container error">
        <p>{{ error }}</p>
      </main>

      <main v-else-if="settlementData" class="content-container">
        <div class="summary-header">
          <p class="trip-name">{{ settlementData.tripName }}</p>
          <h2 class="total-amount">
            총 {{ settlementData.totalAmount?.toLocaleString() || 0 }}원 사용
          </h2>
        </div>

        <!-- 받을 돈 카드 -->
        <div class="settlement-card">
          <div class="card-header">
            <select v-model="selectedMember" class="member-select">
              <option
                v-for="member in settlementData.members"
                :key="member"
                :value="member"
              >
                {{ member }}
              </option>
            </select>
            <span class="card-title">님이 받을 돈</span>
          </div>
          <div class="transaction-list">
            <div v-if="toReceiveList.length > 0">
              <div
                v-for="(tx, index) in toReceiveList"
                :key="index"
                class="transaction-item"
              >
                <div class="member-info">
                  <div class="avatar bg-theme-secondary">
                    <span>{{ tx.senderNickname.substring(0, 1) }}</span>
                  </div>
                  <span>{{ tx.senderNickname }}</span>
                </div>
                <span class="amount">{{ tx.amount.toLocaleString() }}원</span>
              </div>
            </div>
            <p v-else class="empty-message">받을 돈이 없습니다.</p>
          </div>
        </div>

        <!-- 보낼 돈 카드 -->
        <div class="settlement-card">
          <div class="card-header">
            <select v-model="selectedMember" class="member-select">
              <option
                v-for="member in settlementData.members"
                :key="member"
                :value="member"
              >
                {{ member }}
              </option>
            </select>
            <span class="card-title">님이 보낼 돈</span>
          </div>
          <div class="transaction-list">
            <div v-if="toSendList.length > 0">
              <div
                v-for="(tx, index) in toSendList"
                :key="index"
                class="transaction-item"
              >
                <div class="member-info">
                  <div class="avatar bg-theme-primary">
                    <span>{{ tx.receiverNickname.substring(0, 1) }}</span>
                  </div>
                  <span>{{ tx.receiverNickname }}</span>
                </div>
                <span class="amount">{{ tx.amount.toLocaleString() }}원</span>
              </div>
            </div>
            <p v-else class="empty-message">보낼 돈이 없습니다.</p>
          </div>
        </div>
      </main>

      <footer class="footer">
        <button @click="handleRequestSettlement" class="next-button">
          정산 요청 보내기
        </button>
      </footer>
    </div>
  </div>
</template>

<style scoped>
/* 테마 색상 변수 */
.settlement-view {
  --theme-primary: rgba(255, 209, 102, 0.65);
  --theme-secondary: rgba(162, 210, 255, 0.65); /* 새로운 색상 추가 */
  --theme-primary-dark: #e2c05e;
  --theme-bg: #f8f9fa;
  --theme-text: #333333;
  --theme-text-light: #888888;
}

/* 화면 중앙 정렬을 위한 wrapper 스타일 */
.view-wrapper {
  display: flex;
  justify-content: center;
  width: 100%;
  min-height: 100vh;
  background-color: #ffffff;
  padding: 2rem 0;
}

/* 전체 레이아웃 */
.settlement-view {
  z-index: 1;
  width: 100%;
  max-width: 24rem; /* 384px */
  background-color: var(--theme-bg);
  display: flex;
  flex-direction: column;
  border-radius: 1.5rem;
  box-shadow: 0 25px 50px -12px rgb(0 0 0 / 0.25);
  overflow: hidden;
  position: relative;
  height: 844px;
  max-height: 90vh;
}

/* 메인 콘텐츠 */
.content-container {
  flex-grow: 1;
  padding: 1.25rem;
  overflow-y: auto;
  padding-top: calc(56px + 1.25rem);
}
.loading,
.error {
  display: flex;
  align-items: center;
  justify-content: center;
  text-align: center;
  color: var(--theme-text-light);
  padding-top: 56px;
  flex-grow: 1;
}

/* 상단 요약 */
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

/* 정산 카드 */
.settlement-card {
  background-color: white;
  border-radius: 1rem;
  padding: 1.25rem;
  box-shadow: 0 1px 3px 0 rgb(0 0 0 / 0.1), 0 1px 2px -1px rgb(0 0 0 / 0.1);
  margin-bottom: 1rem;
}
.card-header {
  display: flex;
  align-items: center;
  margin-bottom: 1rem;
}
.member-select {
  font-weight: 700;
  font-size: 1.125rem;
  color: var(--theme-text);
  border: none;
  background: transparent;
  -webkit-appearance: none;
  -moz-appearance: none;
  appearance: none;
  padding-right: 1.5rem;
  background-image: url('data:image/svg+xml;charset=UTF-8,<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 20 20" fill="currentColor"><path fill-rule="evenodd" d="M5.293 7.293a1 1 0 011.414 0L10 10.586l3.293-3.293a1 1 0 111.414 1.414l-4 4a1 1 0 01-1.414 0l-4-4a1 1 0 010-1.414z" clip-rule="evenodd" /></svg>');
  background-repeat: no-repeat;
  background-position: right 0.25rem center;
  background-size: 1.25rem;
  cursor: pointer;
}
.card-title {
  font-weight: 600;
  color: var(--theme-text-light);
  margin-left: 0.25rem;
}

/* 거래 내역 리스트 */
.transaction-list {
  display: flex;
  flex-direction: column;
  gap: 1rem;
}
.transaction-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-top: 0.4em;
}
.member-info {
  display: flex;
  align-items: center;
  gap: 0.75rem;
}
.avatar {
  width: 2.5rem;
  height: 2.5rem;
  border-radius: 9999px;
  display: flex;
  align-items: center;
  justify-content: center;
}
.avatar span {
  font-weight: 700;
  font-size: 1.125rem;
  color: white;
}
.amount {
  font-weight: 700;
  font-size: 1.125rem;
  color: var(--theme-text);
}
.empty-message {
  text-align: center;
  color: var(--theme-text-light);
  font-size: 0.875rem;
  padding: 1rem 0;
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

/* 유틸리티 클래스 */
.bg-theme-primary {
  background-color: var(--theme-primary);
}
.bg-theme-secondary {
  background-color: var(--theme-secondary);
}
</style>
