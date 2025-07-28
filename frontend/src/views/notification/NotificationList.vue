<script setup>
import { ref, onMounted } from 'vue'
import axios from 'axios'
import { ChevronDown, ChevronRight } from 'lucide-vue-next'

const userId = 1
const notifications = ref([])
const showDropdown = ref(false)
const selectedLabel = ref('전체')

const tabs = [
  { label: '전체', value: 'ALL' },
  { label: '결제', value: 'TRANSACTION' },
  { label: '정산', value: 'SETTLEMENT' },
  { label: '그룹', value: 'INVITE' }
]

const toggleDropdown = () => {
  showDropdown.value = !showDropdown.value
}

const selectCategory = (tab) => {
  selectedLabel.value = tab.label
  showDropdown.value = false
  fetchNotifications(tab.value)
}

const fetchNotifications = async (category = 'ALL') => {
  try {
    const categoryQuery = category === 'ALL' ? '' : `?category=${category}`
    const { data } = await axios.get(`/api/notifications/${userId}${categoryQuery}`)
    notifications.value = data
  } catch (e) {
    console.error('알림 조회 실패:', e)
  }
}

const getMessage = (n) => {
    const user = n.fromUserNickname || '누군가'
    const place = n.merchantName || '알 수 없는 장소'

    switch (n.notificationType) {
        case 'TRANSACTION':
            return `${user}님이 ${place}에서 ${n.amount}원을 결제했습니다.`
        case 'SETTLEMENT':
            return `${user}님이 정산 요청을 보냈습니다.\n정산을 확인하시겠습니까?`
        case 'INVITE':
            if(n.memberStatus === "JOINED"){
                return `${user}님이 ${n.groupName} 그룹에 들어왔습니다.`
            } else if(n.memberStatus === 'LEFT'){
                return `${user}님이 ${n.groupName} 그룹에서 나갔습니다.`
            }
            return `${user}님이 "${n.groupName}" 그룹에 초대하셨습니다.\n여행에 참여하시겠습니까?`
        case 'REMINDER':
            return `${user}님이 정산 알림을 보냈습니다.`
        case 'COMPLETED':
            return `${user}님이 정산을 완료했습니다.`
        default:
            return `${user}님이 새로운 알림을 보냈습니다.`
    }
}

onMounted(fetchNotifications)
</script>

<template>
  <div class="notification-wrapper">
    <br>
    <!-- 드롭다운 -->
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
      <hr>
    </div>
    <br>
    <br>
    <br>
    <!-- 알림 카드 -->
    <div class="notification-card" 
                v-for="n in notifications" 
                :key="n.notificationId"
                @click="handleCardClick">
      <div class="card-content">
            <p class="card-title" v-if="n.notificationType !== 'INVITE'">[{{ n.groupName }}]</p>
            <p class="card-body">{{ getMessage(n) }}</p>
            <p class="card-time">{{ n.sendAt.split('.')[0].substring(0,16) }}</p>
      </div>
      <ChevronRight class="card-arrow" />
    </div>
  </div>
</template>


<style scoped>
.notification-wrapper {
    padding: 16px;
}

.dropdown {
    position: relative;
    margin-bottom: 16px;
}
.sticky-category{
    box-sizing: border-box;
    position: fixed;
    top: 56px;
    left: 50%;
    transform: translateX(-50%);
    width: calc(100% - 32px);
    max-width: 414px;
    background: #f9fafb;
    z-index: 100;
    padding: 8px 23px;
    box-sizing: border-box;
}

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
    align-items: center
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

.notification-card {
    background: #C2DCFF;
    padding: 12px;
    border-radius: 20px;
    margin-bottom: 12px;
    display: flex;                  /* 텍스트와 아이콘 나란히 */
    justify-content: space-between; /* 텍스트 왼쪽, 아이콘 오른쪽 */
    align-items: center;
    cursor: pointer;
}
.card-content {
    display: flex;
    justify-content: space-between;  /* 텍스트는 왼쪽, 화살표는 오른쪽 */
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
    white-space: pre-line;  /* \n 줄바꿈 처리 */
}

.card-time {
    font-size: 11px;
    color: gray;
}
</style>
