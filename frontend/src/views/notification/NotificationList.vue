<script setup>
import { ref, onMounted } from 'vue';
import { storeToRefs } from 'pinia';
import { useNotificationStore } from '@/stores/notificationStore';
import { ChevronDown, ChevronRight } from 'lucide-vue-next';
import Header from '@/components/layout/Header.vue';
// import { useAuthStore } from '@/stores/auth';

// 로그인한 사용자 ID로 변경
const userId = 2; 
// const authStore = useAuthStore();
// const { user } = storeToRefs(authStore);
// const userId = user.value.userId;

const notificationStore = useNotificationStore();
const { notifications } = storeToRefs(notificationStore);

const showDropdown = ref(false);
const selectedLabel = ref('전체');

const tabs = [
  { label: '전체', value: 'ALL' },
  { label: '결제', value: 'TRANSACTION' },
  { label: '정산', value: 'SETTLEMENT' },
  { label: '그룹', value: 'GROUP_EVENT' }
];

const toggleDropdown = () => {
  showDropdown.value = !showDropdown.value;
};

const selectCategory = (tab) => {
  selectedLabel.value = tab.label;
  showDropdown.value = false;

  //테스트
  notificationStore.getNotifications(userId, tab.value);
  // 실제
  // notificationStore.getNotifications(tab.value);
};

const handleCardClick = (n) => {
  notificationStore.readNotification(n.notificationId);
};

const formatAmount = (value) => {
  if (!value && value !== 0) return '';
  return new Intl.NumberFormat('ko-KR').format(value);
};

onMounted(() => {
  // 테스트
  notificationStore.getNotifications(userId);
  // 실제 적용
  // notificationStore.getNotifications()
});

const getMessage = (n) => {
  const user = n.fromUserNickname || '누군가';
  const place = n.merchantName || '알 수 없는 장소';

  switch (n.notificationType) {
    case 'TRANSACTION':
      return `${user}님이 '${place}'에서 \n${formatAmount(n.amount)}원을 결제했습니다.`;

    case 'SETTLEMENT':
      return `${user}님이 정산 요청을 보냈습니다.\n정산을 확인하시겠습니까?`;

    case 'INVITE':
      if (n.memberStatus === 'JOINED') {
        return `${user}님이 "${n.tripName}" 그룹에 참여했어요.`;
      } else if (n.memberStatus === 'LEFT') {
        return `${user}님이 "${n.tripName}" 그룹에서 나갔어요.`;
      } else {
        return `${user}님이 "${n.tripName}" 그룹에 초대하셨습니다.\n여행에 참여하시겠습니까?`;
      }

    case 'REMINDER':
      return `${user}님이 정산 알림을 보냈습니다.`;

    case 'COMPLETED':
      return `${user}님이 정산을 완료했습니다.`;

    default:
      return `${user}님이 새로운 알림을 보냈습니다.`;
  }
};

</script>

<template>
  <div class="layout-wrapper">
    <Header title="알림"/>
    <!-- 드롭다운 고정 -->
    <div class="dropdown sticky-category">
      <button class="dropdown-btn" @click="toggleDropdown">
        {{ selectedLabel }}
        <ChevronDown class="dropdown-icon" />
      </button>
      <ul v-if="showDropdown" class="dropdown-menu">
        <li v-for="tab in tabs" :key="tab.value" @click="selectCategory(tab)">
          {{ tab.label }}
        </li>
      </ul>
      <hr />
    </div>
    <br>
    <!-- 카드 리스트 스크롤 영역 -->
    <main class="scroll-area">
      <div class="notification-card"
           v-for="n in notifications"
           :key="n.notificationId"
           :class="{'read': n.isRead}"
           @click="handleCardClick(n)">
        <div class="card-content">
          <p class="card-title" v-if="n.notificationType !== 'INVITE' || n.memberStatus === 'JOINED' || n.memberStatus === 'LEFT'">
            [{{ n.tripName }}]
          </p>
          <p class="card-body">{{ getMessage(n) }}</p>
          <p class="card-time">{{ n.sendAt.split('.')[0].substring(0, 16) }}</p>
        </div>
        <ChevronRight class="card-arrow" />
      </div>
    </main>
  </div>
</template>

<style scoped>

@import url('https://fonts.googleapis.com/css2?family=IBM+Plex+Sans+KR:wght@300;400;500;600;700&display=swap');

* {
  font-family: 'IBM Plex Sans KR', sans-serif;
}

/* 전체 레이아웃 */
.layout-wrapper {
  display: flex;
  flex-direction: column;
  align-items: center;
  background-color: #f9fafb;
  width: 100%;
  height: 100vh;
  overflow: hidden;
}

/* 드롭다운 고정 */
.sticky-category {
  position: fixed;
  top: 56px; /* 헤더 높이 */
  left: 50%;
  transform: translateX(-50%);
  width: calc(100% - 32px);
  max-width: 414px;
  background: #f9fafb;
  z-index: 150;
  padding: 8px 23px;
  box-sizing: border-box;
}

/* 카드 스크롤 영역 */
.scroll-area {
  overflow-y: auto;
  width: 100%;
  max-width: 414px;
  padding: 120px 16px 16px; /* 헤더(56px) + 드롭다운 높이 확보 */
  box-sizing: border-box;
  flex: 1;
}

/* 드롭다운 스타일 */
.dropdown-btn {
  font-size: 14px;
  width: 100%;
  padding: 10px 15px;
  border: none;
  border-radius: 15px;
  background-color: #f9fafb;
  text-align: left;
  cursor: pointer;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.dropdown-icon {
  width: 18px;
  height: 18px;
}

.dropdown-menu {
  font-size: 14px;
  position: absolute;
  top: 60%;
  width: 40%;
  text-align: center;
  border: 1px solid #ddd;
  border-radius: 15px;
  background: #fff;
  list-style: none;
  margin: 4px 0 0;
  padding: 0;
}

.dropdown-menu li {
  padding: 10px;
  cursor: pointer;
}

.dropdown-menu li:hover {
  background-color: #f0f0f0;
}

/* 카드 스타일 */
.notification-card {
  background: #fff;
  padding: 14px 12px;
  border-bottom: 1px solid #e5e7eb; /* 리스트 느낌 */
  display: flex;
  justify-content: space-between;
  align-items: center;
  cursor: pointer;
  transition: background-color 0.2s ease;
}

.notification-card.read {
  background: #E5E7EB;
  opacity: 0.7;
}

.card-content {
  display: flex;
  flex-direction: column;
  flex: 1;
}

.card-arrow {
  width: 20px;
  height: 20px;
  color: #555;
}

.notification-card p {
  margin: 3px;
}

.card-title {
  font-size: 13px;
}

.card-body {
  font-size: 13px;
  white-space: pre-line;
}

.card-time {
  font-size: 11px;
  color: gray;
}
</style>

