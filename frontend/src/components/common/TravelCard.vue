<script setup>
import { ref, computed } from 'vue';
import { FileChartColumn, Trash2, TriangleAlert } from 'lucide-vue-next';
import { useRouter, useRoute } from 'vue-router';
import apiClient from '@/api/index.js';

const props = defineProps({
  tripId: Number,
  tripName: String,
  startDate: String,
  endDate: String,
  activeTab: String,
  tripStatus: String,
  isOwner: Boolean,
  onDelete: Function,
});

const emit = defineEmits(['update:activeTab']);

const formattedDate = computed(() => {
  const start = props.startDate?.replaceAll('-', '.') ?? '';
  const end = props.endDate?.replaceAll('-', '.') ?? '';
  return start && end ? `${start} ~ ${end}` : '';
});

const tabs = ['그룹 지출 내역', '선결제 내역', '그룹 관리'];
// const activeTab = ref(tabs[0]);

function selectTab(tab) {
  emit('update:activeTab', tab);
}

const router = useRouter();
const route = useRoute();

// 아이콘 클릭 시 차트 페이지로 이동
async function goToChart() {
  try {
    const tripId = route.params.tripId;
    const res = await apiClient.get(`/report/${tripId}/is-member`);
    const isMember = res.data;
    console.log(isMember);

    if (isMember) {
      router.push({ name: 'report', params: { tripId } });
    } else {
      alert('현재 여행에 포함된 회원이 아닙니다');
    }
  } catch (error) {
    console.error('멤버 확인 중 오류 발생:', error);
    alert('서버 오류가 발생했습니다. 다시 시도해주세요.');
  }
}

// 상태: 여행 종료 모달
const showDeleteModal = ref(false)

// 여행 종료 모달 열기
const openDeleteModal = () => {
  showDeleteModal.value = true
}

// 여행 종료 모달 취소
const cancelDelete = () => {
  showDeleteModal.value = false
}

// 여행 종료 확인
const confirmDelete = () => {
  showDeleteModal.value = false
  if (props.onDelete) {
    props.onDelete()
  }
}

</script>

<template>
  <div class="travel-card">
    <!-- 카드 헤더 -->
    <div class="card-header">
      <!-- 왼쪽: 여행 제목 + 날짜 -->
      <div class="title-date">
        <div class="title-row">
          <div class="trip-name">{{ props.tripName }}</div>
          <div v-if="props.tripStatus === 'CLOSED'" class="icon-wrapper">
            <FileChartColumn class="icon" @click="goToChart" />
            <span class="red-dot"></span>
          </div>
        </div>
        <div class="trip-date">{{ formattedDate }}</div>
      </div>

      <!-- 오른쪽: 휴지통 아이콘 -->
      <Trash2
          class="icon trash-icon"
          @click="openDeleteModal"
          v-if="tripStatus === 'READY' && isOwner === true"
      />
    </div>

    <!-- 카드 구분선 -->
    <div class="divider"></div>

    <!-- 탭 목록 -->
    <div class="tab-list">
      <div
        v-for="tab in tabs"
        :key="tab"
        class="tab-item"
        :class="{ active: activeTab === tab }"
        @click="selectTab(tab)"
      >
        {{ tab }}
      </div>
    </div>
    <div
        v-if="showDeleteModal"
        class="modal-overlay"
        @click="cancelDelete"
    >
      <div class="delete-modal" @click.stop>
        <!-- 아이콘 -->
        <div class="modal-icon"></div>

        <!-- 메인 메시지 -->
        <h3 class="modal-title">
          <TriangleAlert />  정말로 여행을 삭제하시겠습니까?
        </h3>

        <!-- 설명 텍스트 -->
        <p class="modal-description">
          지금까지의 모든 내역이 사라집니다.
        </p>

        <!-- 버튼들 -->
        <div class="modal-buttons">
          <button @click="cancelDelete" class="modal-cancel-btn">취소하기</button>
          <button @click="confirmDelete" class="modal-confirm-btn">삭제하기</button>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.travel-card {
  width: 100%;
  max-width: 343px;
  margin: 0 auto 1rem auto;
  padding: 0;
  box-sizing: border-box;
  border-radius: 16px;
  font-family: 'IBM Plex Sans KR', sans-serif;
  display: flex;
  flex-direction: column;
  justify-content: space-between;

  background: white;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 10px;
  padding: 16px;
}

.title-date {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.title-row {
  display: flex;
  align-items: center;
  /*gap: 8px;*/
  /* justify-content: space-between; */
}

.trip-name {
  color: #000;
  font-size: 24px;
  font-weight: 1000;
  margin: 0;
  margin-right: 5px;
}

.trip-date {
  color: #6d6d6d;
  font-size: 15px;
  opacity: 0.8;
}

.icon {
  width: 20px;
  height: 20px;
  color: #666;
  cursor: pointer;
  transition: color 0.2s;
}

.icon:hover {
  color: #333;
}

.trash-icon {
  margin-top: 8px;
}

.divider {
  width: 100%;
  height: 1px;
  background-color: #666;
  margin: 8px 0;
  opacity: 0.1;
}

.tab-list {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 8px;
  padding: 0 16px 8px 16px;
}

.tab-item {
  flex: 1;
  padding: 4px 8px;
  border-radius: 12px;
  font-size: 16px;
  font-weight: 600;
  text-align: center;
  white-space: nowrap;
  cursor: pointer;
  transition: background-color 0.2s;
  color: #575757;
}

.tab-item:hover {
  background-color: rgba(0, 0, 0, 0.05);
}

.tab-item.active {
  color: #fdb100;
}

.icon-wrapper {
  position: relative;
  width: 24px;
  height: 24px;
}

.icon {
  width: 20px;
  height: 20px;
  color: #666;
  cursor: pointer;
  transition: color 0.2s;
}

/* 빨간 알림 점 */
.red-dot {
  position: absolute;
  top: -2px;
  right: -2px;
  width: 7px;
  height: 7px;
  background-color: #ff6b6b; /* 연한 빨간색 */
  border-radius: 50%;
  border: 2px solid white; /* 외곽 경계 */
}

.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100vw;
  height: 100vh;
  background-color: rgba(0, 0, 0, 0.6);
  display: flex;
  justify-content: center;
  align-items: flex-end;
  z-index: 1100;
  animation: fadeIn 0.3s ease-out;
  backdrop-filter: blur(2px);
  -webkit-backdrop-filter: blur(2px);
}

.delete-modal {
  width: 100%;
  max-width: 325px;
  height: auto;
  min-height: 230px;
  background: white;
  border-radius: 1.5rem;
  box-shadow: 0 -4px 32px rgba(0, 0, 0, 0.24);
  padding: 28px 40px 36px 40px;
  position: relative;
  animation: slideUpFromBottom 0.3s ease-out;
}

.modal-icon {
  width: 40px;
  height: 40px;
  font-size: 28px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 0 auto 20px auto;
}

.modal-title {
  display: flex;
  align-items: center;
  justify-content: center;
  color: #1f2937;
  font-size: 20px;
  font-weight: 700;
  line-height: 28px;
  margin: 0 0 16px 0;
}

.modal-description {
  text-align: center;
  color: #6b7280;
  font-size: 14px;
  font-weight: 500;
  line-height: 20px;
  margin: 0 0 28px 0;
}

.modal-buttons {
  display: flex;
  gap: 12px;
  justify-content: center;
  width: 100%;
  max-width: 400px;
  margin: 0 auto;
}

.modal-cancel-btn,
.modal-confirm-btn {
  flex: 1;
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

/* 애니메이션 */
@keyframes fadeIn {
  from {
    opacity: 0;
  }
  to {
    opacity: 1;
  }
}

@keyframes slideUpFromBottom {
  from {
    transform: translateY(100%);
    opacity: 0;
  }
  to {
    transform: translateY(0);
    opacity: 1;
  }
}

/* 반응형 대응 */
@media (max-width: 480px) {
  .delete-modal {
    padding: 24px 20px 32px 20px;
    margin: 0 auto;
    border-radius: 16px;
    margin-bottom: 2rem;
  }

  .modal-buttons {
    gap: 8px;
    max-width: none;
  }

  .modal-cancel-btn,
  .modal-confirm-btn {
    height: 44px;
    font-size: 15px;
  }
}

@media (min-width: 768px) {
  .delete-modal {
    max-width: 325px;
    margin: 0 auto;
    border-radius: 16px;
    margin-bottom: 2rem;
  }
}
</style>
