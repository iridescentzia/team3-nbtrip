<script setup>
import { ref, onMounted } from 'vue';
import { storeToRefs } from 'pinia';
import { useRouter } from 'vue-router';
import { useNotificationStore } from '@/stores/notificationStore';
import { ChevronDown, ChevronRight } from 'lucide-vue-next';
import Header from '@/components/layout/Header.vue';

const router = useRouter();
const goBack = () => {
  router.back();
};

const notificationStore = useNotificationStore();
const { notifications } = storeToRefs(notificationStore);

const showDropdown = ref(false);
const selectedLabel = ref('전체');

const goToPage = (n) => {

  switch (n.notificationType) {
    case 'TRANSACTION':
      // 결제 상세 페이지로 이동
      if (n.paymentId) {
        router.push(`/paymentlist/${n.tripId}`);
      }
      break;
    case 'SETTLEMENT':
      router.push(`/settlement/${n.tripId}/detail`);
      break;
    case 'COMPLETED':
      router.push(`/settlement/${n.tripId}/completed`);
      break;
    case 'REMINDER':
      router.push(`/settlement/${n.tripId}/detail`);
      break;
    case 'INVITE':
      router.push(`/trip/join/${n.tripId}`)
      // if (!n.memberStatus) {
      //   router.push(`/trip/join/${n.tripId}`)
      // } else {
      //   router.push(`/trip/${n.tripId}`)
      // }
      break;

    case 'GROUP_EVENT':
      // 그룹 상세 페이지
      router.push(`/trip/${n.tripId}`);
      break;
    default:
      alert('지원하지 않는 알림 유형입니다.');
  }
};

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

  notificationStore.getNotifications(tab.value);
};

const handleCardClick = (n) => {
  notificationStore.readNotification(n.notificationId);
  // goToPage(n); // 이동까지 같이 처리
};

const formatAmount = (value) => {
  if (!value && value !== 0) return '';
  return new Intl.NumberFormat('ko-KR').format(value);
};

onMounted(() => {
  notificationStore.getNotifications()
      .then(() => {
        console.log('notifications:', notifications.value);
      })
});

const getMessage = (n) => {
  const user = n.fromUserNickname || '누군가';
  const place = n.merchantName || '알 수 없는 장소';

  switch (n.notificationType) {
    case 'TRANSACTION':
      const isUpdate = n.actionType === 'UPDATE';
      return isUpdate
          ? `${user}님이 '${place}'결제 내역을 수정했습니다.`
          : `${user}님이 '${place}'에서 \n${formatAmount(n.amount)}원을 결제했습니다.`;

    case 'SETTLEMENT':
      return `${user}님이 정산 요청을 보냈습니다.\n정산을 확인하시겠습니까?`;

    case 'INVITE':
      return `${user}님이 "${n.tripName}" 그룹에 초대하셨습니다.\n여행에 참여하시겠습니까?`;

    case 'GROUP_EVENT':
      if (n.memberStatus === 'JOINED') {
        return `${user}님이 "${n.tripName}" 그룹에 참여했어요.`;
      } else if (n.memberStatus === 'LEFT') {
        return `${user}님이 "${n.tripName}" 그룹에서 나갔어요.`;
      } else {
        return `${user}님이 그룹 관련 활동을 했습니다.`;
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
  <div class="notification-content">
    <div class="header-section">
      <Header title="알림" :backAction="goBack"/>

      <div class="dropdown-wrapper">
        <button class="dropdown-toggle" @click="toggleDropdown">
          {{ selectedLabel }}
          <ChevronDown class="icon" />
        </button>
        <ul v-if="showDropdown" class="dropdown-list">
          <li v-for="tab in tabs" :key="tab.value" @click="selectCategory(tab)">
            {{ tab.label }}
          </li>
        </ul>
      </div>
      <div class="dropdown-divider"></div>
    </div>

    <!-- 카드 리스트 스크롤 영역 -->
    <div class="scroll">
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
          <ChevronRight class="card-arrow" @click.stop="goToPage(n)"/>
        </div>
      </main>
    </div>
  </div>



</template>

<style scoped>
.notification-content{
  width: 100%;
  height: 100%;
  display: flex;
  flex-direction: column;
  background: #f8fafc;
  overflow: hidden;
  font-family: 'IBM Plex Sans KR', sans-serif;
}
.header-section{
  margin-right: 14px;
  margin-left: 14px;
  margin-bottom: 20%;
}

.dropdown-wrapper {
  background: #f8fafc;
  font-size: 14px;
  margin: 60px 0;
  width: 100%;
  display: flex;
  position: relative;
}
.dropdown-toggle {
  width: 100%;
  padding: 12px 16px;
  font-size: 14px;
  background: #f8fafc;
  border: 1px #ccc;
  border-radius: 10px;
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.dropdown-list {
  position: absolute;
  top: 20%;
  width: 40%;
  margin: 0;
  padding: 0;
  text-align: center;
  background-color: #fff;
  border-radius: 15px;
  border: 1px solid #ddd;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.05);
  list-style: none;
  z-index: 9999;
}
.dropdown-list li {
  padding: 12px;
  cursor: pointer;
}
.dropdown-list li:hover {
  background-color: #f0f0f0;
}

.dropdown-divider {
  width: 100%;
  height: 1px;
  background-color: #babec4;
  margin-top: -60px;
}

.icon{
  width: 20px;
  height: 20px;
}

.scroll{
  flex: 1;
  overflow-y: auto;
  margin-bottom: 1%;
  border-radius: 1.5rem;
}

.notification-card {
  background: #fff;
  padding: 14px 14px;
  margin-right: 1%;
  margin-left: 1%;

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
