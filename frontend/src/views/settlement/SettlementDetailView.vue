<script setup>
import { onMounted, computed } from 'vue';
import { storeToRefs } from 'pinia';
import Header from '../../components/layout/Header3.vue';
import { useSettlementStore } from '@/stores/settlementStore';
import { useRoute, useRouter } from 'vue-router';
import { SquareCheckBig } from 'lucide-vue-next';

// ✅ Pinia Store 사용
const settlementStore = useSettlementStore();
const {
  mySettlementData,
  isLoading,
  error,
  showTransferModal,
  totalReceiveAmount,
  totalSendAmount,
  netBalance,
} = storeToRefs(settlementStore);

const route = useRoute();
const router = useRouter();
const tripId = route.params.tripId;

// 데이터 로딩
onMounted(async () => {
  try {
    await settlementStore.fetchMySettlement(tripId);
  } catch (err) {
    // 에러 처리는 store에서 담당
  }
});

// 버튼 상태를 위한 계산된 속성 추가
const buttonState = computed(() => {
  const hasSendAmount = totalSendAmount.value > 0;
  const hasReceiveAmount = totalReceiveAmount.value > 0;

  if (hasSendAmount) {
    // 보낼 돈이 있는 경우
    return {
      text: '송금하기',
      action: 'transfer',
      disabled: false,
    };
  } else if (hasReceiveAmount) {
    // 보낼 돈은 없고 받을 돈만 있는 경우
    return {
      text: '확인',
      action: 'confirm',
      disabled: false,
    };
  } else {
    // 보낼 돈도 받을 돈도 없는 경우
    return {
      text: '확인',
      action: 'confirm',
      disabled: false,
    };
  }
});

// ✅ 송금하기 버튼 클릭
const handleButtonClick = () => {
  if (buttonState.value.action === 'transfer') {
    // 송금하기 버튼 동작 (기존 로직)
    try {
      settlementStore.openTransferModal();
    } catch (err) {
      alert(err.message);
    }
  } else if (buttonState.value.action === 'confirm') {
    // 확인 버튼 동작 (홈으로 이동)
    goHome();
  }
};

// 홈으로 이동하는 함수 추가
const goHome = () => {
  // Store 데이터 정리 후 홈으로 이동
  settlementStore.clearMySettlementData();
  router.push('/');
};

// ✅ 모달 취소
const cancelTransfer = () => {
  settlementStore.closeTransferModal();
};

// ✅ 실제 송금 실행
const confirmTransfer = async () => {
  settlementStore.closeTransferModal();

  try {
    const transferResult = await settlementStore.executeTransfer();

    if (transferResult.failedCount > 0) {
      router.push(`/settlement/${tripId}/failure`);
      return;
    }

    // 송금 후 최신 정산 상태 재조회
    await settlementStore.fetchMySettlement(tripId);

    // 받을 돈이 아직 미완료 상태인지 확인
    const hasIncompleteToReceive =
      mySettlementData.value?.toReceive?.some(
        (tx) => tx.status !== 'COMPLETED'
      ) || false;

    if (hasIncompleteToReceive) {
      // 송금 완료 + 받을 돈 미완료 → pending
      router.push(`/settlement/${tripId}/pending`);
    } else {
      // 송금 완료 + 받을 돈 완료 → completed
      router.push(`/settlement/${tripId}/completed`);
    }
  } catch (err) {
    router.push(`/settlement/${tripId}/failure`);
  }
};

</script>

<template>
  <div class="settlement-view">
    <Header title="정산하기" />

    <main v-if="isLoading" class="content-container loading">
      <p>내 정산 내역을 불러오는 중...</p>
    </main>
    <main v-else-if="error" class="content-container error">
      <p>{{ error }}</p>
    </main>

    <main v-else-if="mySettlementData" class="content-container">
      <div class="summary-header">
        <p class="trip-name">{{ mySettlementData.tripName }}</p>
        <h2 class="total-amount">
          총 {{ mySettlementData.totalAmount?.toLocaleString() || 0 }}원 사용
        </h2>
      </div>

      <!-- 받을 돈 카드 -->
      <div class="settlement-card">
        <p class="card-title text-theme-text">받을 돈</p>
        <div class="transaction-list">
          <div
            v-if="
              mySettlementData.toReceive &&
              mySettlementData.toReceive.length > 0
            "
          >
            <div
              v-for="tx in mySettlementData.toReceive"
              :key="tx.settlementId"
              class="transaction-item"
            >
              <div class="member-info">
                <div class="avatar">
                  <span>{{ tx.senderNickname?.substring(0, 1) || '?' }}</span>
                </div>
                <div class="member-text">
                  <div class="name-with-badge">
                    <span class="font-semibold text-sm text-theme-text">
                      {{ tx.senderNickname || '알 수 없음' }}
                    </span>
                    <div v-if="tx.status === 'COMPLETED'" class="status-badge completed">
                      <SquareCheckBig class="status-icon" />
                      <span class="status-text">받음</span>
                    </div>
                  </div>
                </div>
              </div>
              <span class="amount text-theme-text">
                {{ tx.amount?.toLocaleString() || 0 }}원
              </span>
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
            v-if="mySettlementData.toSend && mySettlementData.toSend.length > 0"
          >
            <div
              v-for="tx in mySettlementData.toSend"
              :key="tx.settlementId"
              class="transaction-item"
            >
              <div class="member-info">
                <div class="avatar">
                  <span>{{ tx.receiverNickname?.substring(0, 1) || '?' }}</span>
                </div>
                <div class="member-text">
                  <div class="name-with-badge">
                    <span class="font-semibold text-sm">
                      {{ tx.receiverNickname || '알 수 없음' }}
                    </span>
                    <div v-if="tx.status === 'COMPLETED'" class="status-badge completed">
                      <SquareCheckBig class="status-icon" />
                      <span class="status-text">보냄</span>
                    </div>
                  </div>
                </div>
              </div>
              <span class="amount">
                {{ tx.amount?.toLocaleString() || 0 }}원
              </span>
            </div>
          </div>
          <p v-else class="empty-message">보낼 돈이 없습니다.</p>
        </div>
      </div>
    </main>

    <footer class="footer">
      <button
        @click="handleButtonClick"
        class="next-button"
        :disabled="buttonState.disabled"
      >
        {{ buttonState.text }}
      </button>
    </footer>
  </div>

  <!-- ✅ 송금하기 모달  -->
  <div
      v-if="showTransferModal && buttonState.action === 'transfer'"
      class="modal"
  >
    <!-- 메인 콘텐츠 -->
    <div
        style="
      width: calc(100% - 32px);
      text-align: center;
      margin: 0 auto 24px auto;
    "
    >
      <h3 style="font-size: 22px; font-weight: bold; color: #34495e; margin: 0 0 12px 0">
        송금하시겠습니까?
      </h3>
      <p style="color: #6b7280; font-size: 14px; margin: 0;">
        송금 버튼을 누르면 정산이 완료되며,<br />
        되돌릴 수 없습니다.
      </p>
    </div>

    <!-- 버튼들 -->
    <div
        style="
      width: calc(100% - 32px);
      height: 48px;
      display: flex;
      justify-content: center;
      gap: 8px;
      margin: 0 auto;
    "
    >
      <button
          @click="cancelTransfer"
          class="modal-cancel-btn"
          style="margin-right: 8px; flex: 1;"
      >
        취소
      </button>
      <button
          @click="confirmTransfer"
          class="modal-confirm-btn"
          style="flex: 1;"
      >
        송금
      </button>
    </div>
  </div>
</template>

<style scoped>
/* 전체 레이아웃 */
.settlement-view {
  width: 100%;
  height: 100%;
  background-color: var(--theme-bg);
  display: flex;
  flex-direction: column;
  position: relative; /* Header의 absolute 포지션 기준점 */
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

/* ✅ 상태별 거래 아이템 배경색 */
.transaction-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 6px;
  border-radius: 8px;
  transition: all 0.2s ease;
}

/* ✅ 멤버 정보 레이아웃 */
.member-info {
  display: flex;
  align-items: center;
  gap: 0.75rem;
}

.member-text {
  display: flex;
  flex-direction: column;
  gap: 0.25rem;
}

.name-with-badge {
  display: flex;
  align-items: center;
  gap: 0.5rem; /* 이름과 배지 사이 간격 */
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

/* ✅ 상태 배지 - 아이콘 + 텍스트 (초록 계열) */
.status-badge {
  display: flex;
  align-items: center;
  gap: 0.25rem; /* 아이콘과 텍스트 사이 간격 */
  padding: 2px 6px;
  border-radius: 4px;
  font-weight: 600;
}

.status-badge.completed {
  background-color: #dcfce7; /* 연한 초록 배경 */
  color: #16a34a; /* 진한 초록 텍스트 */
}

.status-icon {
  width: 12px; /* 아이콘 크기 */
  height: 12px;
  color: #16a34a; /* 초록색 아이콘 */
}

.status-text {
  font-size: 0.75rem; /* 12px */
  font-weight: 600;
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

.modal {
  position: absolute;
  left: 50%;
  transform: translateX(-50%);
  bottom: 0;
  width: 352px;
  background-color: #ffffff;
  border-radius: 16px 16px 0 0;
  padding: 16px 16px 24px 16px;
  box-shadow: 0 -4px 16px rgba(0, 0, 0, 0.15);
  z-index: 1001;
  animation: modalUp 0.25s ease;
}

@keyframes modalUp {
  from {
    bottom: -300px;
    opacity: 0;
  }
  to {
    bottom: 0;
    opacity: 1;
  }
}

/* 버튼 스타일 */
.modal-cancel-btn,
.modal-confirm-btn {
  height: 48px;
  background: rgba(255, 209, 102, 0.65);
  border-radius: 12px;
  border: none;
  color: #374151;
  font-size: 16px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.2s ease;
  display: flex;
  align-items: center;
  justify-content: center;
}

.modal-cancel-btn:hover,
.modal-confirm-btn:hover {
  opacity: 0.8;
  transform: translateY(-1px);
}

.modal-cancel-btn:active,
.modal-confirm-btn:active {
  transform: translateY(0);
}
</style>
