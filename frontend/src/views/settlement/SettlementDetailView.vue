<script setup>
import { ref, onMounted, computed } from 'vue';
import Header from '../../components/layout/Header2.vue';
import { getMySettlementDetails, transferMoney } from '@/api/settlementApi';
import { useRoute, useRouter } from 'vue-router';

// --- 상태 관리 ---
const settlementData = ref(null);
const isLoading = ref(true);
const error = ref(null);

// --- 라우터 ---
const route = useRoute();
const router = useRouter();
const tripId = route.params.tripId;
// userId는 JWT 토큰을 통해 서버에서 자동으로 인식하므로, 프론트에서 보낼 필요 X

// --- 데이터 로딩 ---
onMounted(async () => {
  try {
    const response = await getMySettlementDetails(tripId);
    settlementData.value = response.data;
  } catch (err) {
    console.error('개인 정산 정보 로딩 실패:', err);
    error.value = '데이터를 불러오는 데 실패했습니다.';
  } finally {
    isLoading.value = false;
  }
});

// --- 계산된 속성 (Computed Properties) ---
const totalReceiveAmount = computed(() => {
  if (!settlementData.value?.toReceive) return 0;
  return settlementData.value.toReceive.reduce((sum, tx) => sum + tx.amount, 0);
});

const totalSendAmount = computed(() => {
  if (!settlementData.value?.toSend) return 0;
  return settlementData.value.toSend.reduce((sum, tx) => sum + tx.amount, 0);
});

const netBalance = computed(() => {
  return totalReceiveAmount.value - totalSendAmount.value;
});

// 송금하기 함수
const handleTransfer = async () => {
  if (totalSendAmount.value === 0) {
    alert('보낼 돈이 없습니다.');
    return;
  }
  if (!confirm(`총 ${totalSendAmount.value.toLocaleString()}원을 송금할까요?`))
    return;

  try {
    const settlementIdsToSend = settlementData.value.toSend.map(
      (tx) => tx.settlementId
    );

    // API 응답 받기
    const response = await transferMoney({settlementIds: settlementIdsToSend});
    const transferResult = response.data;

    // 송금 결과에 따른 페이지 이동
    if(transferResult.failedCount == 0) {
      // 모든 송금 성공
      alert('모든 송금이 완료되었습니다.');
      router.push(`/settlement/${tripId}/pending`);
    } else {
      // 1건이라도 실패
      alert(`송금 실패: ${transferResult.successCount}건 성공, ${transferResult.failedCount}건 실패`);
      router.push(`/settlement/${tripId}/failure`);
    }
  } catch (err) {
    console.error('송금 실패:', err);
    alert('송금 중 오류가 발생했습니다.');
    router.push(`/settlement/${tripId}/failure`);
  }
};
</script>

<template>
  <div class="view-wrapper">
    <div class="settlement-view">
      <Header title="정산하기" />

      <main v-if="isLoading" class="content-container loading">
        <p>내 정산 내역을 불러오는 중...</p>
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
          <p class="card-title text-theme-text">받을 돈</p>
          <div class="transaction-list">
            <div
              v-if="
                settlementData.toReceive && settlementData.toReceive.length > 0
              "
            >
              <div
                v-for="tx in settlementData.toReceive"
                :key="tx.settlementId"
                class="transaction-item"
              >
                <div class="member-info">
                  <div class="avatar">
                    <span>{{ tx.senderNickname.substring(0, 1) }}</span>
                  </div>
                  <span class="font-semibold text-sm text-theme-text">{{tx.senderNickname }}</span>
                </div>
                <span class="amount text-theme-text"
                  >{{ tx.amount.toLocaleString() }}원</span
                >
              </div>
            </div>
            <p v-else class="empty-message text-theme-text">
              받을 돈이 없습니다.
            </p>
          </div>
        </div>

        <!-- 보낼 돈 카드 -->
        <div class="settlement-card">
          <p class="card-title">보낼 돈</p>
          <div class="transaction-list">
            <div
              v-if="settlementData.toSend && settlementData.toSend.length > 0"
            >
              <div
                v-for="tx in settlementData.toSend"
                :key="tx.settlementId"
                class="transaction-item"
              >
                <div class="member-info">
                  <div class="avatar">
                    <span>{{ tx.receiverNickname.substring(0, 1) }}</span>
                  </div>
                  <span class="font-semibold text-sm">{{
                    tx.receiverNickname
                  }}</span>
                </div>
                <span class="amount">{{ tx.amount.toLocaleString() }}원</span>
              </div>
            </div>
            <p v-else class="empty-message">보낼 돈이 없습니다.</p>
          </div>
        </div>
      </main>

      <footer class="footer">
        <button
          @click="handleTransfer"
          class="next-button"
          :disabled="totalSendAmount === 0"
        >
          송금하기
        </button>
      </footer>
    </div>
  </div>
</template>

<style scoped>
/* 테마 색상 변수 */
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
  margin-bottom: 0.5rem;
}
.trip-name {
  color: var(--theme-text-light);
  margin: auto;
}
.total-amount {
  font-size: 1.875rem;
  font-weight: 800;
  color: var(--theme-text);
  margin-top: 0.1rem;
}

/* 정산 카드 */
.settlement-card {
  background-color: white;
  border-radius: 1rem;
  padding: 1.25rem;
  box-shadow: 0 1px 3px 0 rgb(0 0 0 / 0.1), 0 1px 2px -1px rgb(0 0 0 / 0.1);
  margin-bottom: 1rem;
}
.card-title {
  font-weight: 700;
  margin-bottom: 1rem;
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
  padding: 4px;
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
  background-color: var(--theme-primary);
}
.avatar span {
  font-weight: 700;
  font-size: 1.125rem;
  color: white;
}
.font-semibold {
  font-weight: 600;
}
.text-sm {
  font-size: 0.875rem; /* 14px */
}
.amount {
  font-weight: 700;
  font-size: 1.125rem;
}
.empty-message {
  text-align: center;
  color: var(--theme-text-light);
  font-size: 0.875rem;
  padding: 1rem 0;
}

/* 최종 요약 카드 */
.summary-card {
  background-color: white;
  border-radius: 1rem;
  padding: 0.5rem;
  text-align: center;
  box-shadow: 0 1px 3px 0 rgb(0 0 0 / 0.1), 0 1px 2px -1px rgb(0 0 0 / 0.1);
  font-size: 1rem;
}
.font-bold {
  font-weight: 700;
}
.text-lg {
  font-size: 1.125rem; /* 18px */
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
  transition: opacity 0.2s, background-color 0.2s;
  border: none;
  cursor: pointer;
}
.next-button:hover {
  opacity: 0.9;
}
.next-button:disabled {
  background-color: #d1d5db; /* gray-300 */
  cursor: not-allowed;
  opacity: 0.7;
}

/* 유틸리티 클래스 */
.bg-theme-primary {
  background-color: var(--theme-primary);
}
.bg-theme-secondary {
  background-color: var(--theme-secondary);
}
.text-theme-text {
  color: var(--theme-text);
}
.text-white {
  color: white;
}
.text-theme-red {
  color: var(--theme-red);
}
.text-theme-blue {
  color: var(--theme-blue);
}
</style>
