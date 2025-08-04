<script setup>
import { onMounted, computed } from 'vue';
import { storeToRefs } from 'pinia';
import Header from '../../components/layout/Header2.vue';
import { useSettlementStore } from '@/stores/settlementStore';
import { useRoute, useRouter } from 'vue-router';

// ✅ Pinia Store 사용
const settlementStore = useSettlementStore();
const {
  groupSettlementData,
  selectedMember,
  isLoading,
  error,
  toReceiveList,
  toSendList,
} = storeToRefs(settlementStore);

const route = useRoute();
const router = useRouter();
const tripId = route.params.tripId;

// 뒤로가기
const goBackToSummary = () => {
  router.push(`/settlement/${tripId}`);
};

// 데이터 로딩
onMounted(async () => {
  try {
    await settlementStore.fetchGroupSettlement(tripId);
  } catch (err) {
    // 에러 처리는 store에서 담당
  }
});

// 정산 요청 보내기 함수 - 알림 발송 + 페이지 이동
const handleRequestSettlement = async () => {
  if (!confirm('그룹원들에게 정산 요청 알림을 보내시겠습니까?')) return;

  try {
    // 로딩 상태 표시
    settlementStore.isLoading = true;

    // 1. 정산 요청 알림 발송
    await settlementStore.sendNotification(tripId);

    // 2. 성공 메시지 표시
    alert('정산 요청 알림이 모든 그룹원에게 발송되었습니다! 📢');

    // 3. 그룹장의 개인별 정산 상세 페이지로 이동
    router.push(`/settlement/${tripId}/detail`);

  } catch (err) {
    console.error('정산 요청 알림 발송 실패:', err);

    // 에러 타입별 메시지 처리
    if (err.message.includes('권한이 없습니다')) {
      alert('❌ 권한이 없습니다. 그룹장만 정산 요청을 보낼 수 있습니다.');
    } else if (err.message.includes('서버 오류')) {
      alert('❌ 서버 오류가 발생했습니다. 잠시 후 다시 시도해주세요.');
    } else {
      alert('❌ 알림 발송에 실패했습니다. 네트워크 연결을 확인해주세요.');
    }
  } finally {
    // 로딩 상태 해제
    settlementStore.isLoading = false;
  }
};
</script>

<template>
  <div class="settlement-view">
    <Header title="정산하기" :back-action="goBackToSummary" />

    <main v-if="isLoading" class="content-container loading">
      <p>최종 정산 결과를 계산하는 중...</p>
    </main>
    <main v-else-if="error" class="content-container error">
      <p>{{ error }}</p>
    </main>

    <main v-else-if="groupSettlementData" class="content-container">
      <div class="summary-header">
        <p class="trip-name">{{ groupSettlementData.tripName }}</p>
        <h2 class="total-amount">
          총 {{ groupSettlementData.totalAmount?.toLocaleString() || 0 }}원 사용
        </h2>
      </div>

      <!-- 받을 돈 카드 -->
      <div class="settlement-card">
        <div class="card-header">
          <select v-model="selectedMember" class="member-select">
            <option
              v-for="member in groupSettlementData.members"
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
                  <span>{{ tx.senderNickname?.substring(0, 1) || '?' }}</span>
                </div>
                <span>{{ tx.senderNickname || '알 수 없음' }}</span>
              </div>
              <span class="amount"
                >{{ tx.amount?.toLocaleString() || 0 }}원</span
              >
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
              v-for="member in groupSettlementData.members"
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
                  <span>{{ tx.receiverNickname?.substring(0, 1) || '?' }}</span>
                </div>
                <span>{{ tx.receiverNickname || '알 수 없음' }}</span>
              </div>
              <span class="amount"
                >{{ tx.amount?.toLocaleString() || 0 }}원</span
              >
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
