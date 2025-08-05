<script setup>
import Header from "@/components/layout/Header.vue";
import TravelCard2 from "@/components/common/TravelCard2.vue";
import tripApi from "@/api/tripApi.js";
import memberApi from "@/api/memberApi.js";
import {ref} from "vue";
import {useRoute} from "vue-router";
import VueDatePicker from '@vuepic/vue-datepicker';

const tripDetail = ref({
  tripName: '',
  startDate: '',
  endDate: '',
});
const tripStatus = ref('');
const headerTitle = ref('');
const route = useRoute();
const disableDates = ref([]);
const date = ref({});
const members = ref([]);
const newTitle = ref("");

const load = async () => {
  const data = await tripApi.getTripDetail(route.params.tripId);
  if (!data) return;
  try {
    disableDates.value = await tripApi.getDisabledDates(Number(route.params.tripId));
  } catch (e) {
    console.error('비활성화 날짜 불러오기 실패:', e);
  }
  tripDetail.value = data;
  tripStatus.value = data.tripStatus;

  headerTitle.value =
      data.tripStatus === 'READY'
          ? '예정된 여행 상세'
          : data.tripStatus === 'ACTIVE'
              ? '진행 중인 여행'
              : '지난 여행 상세';


  // MemberResponseDTO 정보 + 현재 회원상태 적용
  members.value = await Promise.all(
      data.members.map(async (member) => {
        const userInfo = await memberApi.getUserInfo(member.userId);
        return {
          ...userInfo,
          memberId: member.memberId,
          tripId: member.tripId,
          status: member.memberStatus
        };
      })
  );
  console.log(data.members);
  console.log(members.value);
};

const formatDate = (date) => {
  if (!date) return null;
  return date.toISOString().split('T')[0];  // yyyy-MM-dd
};

const handleUpdate = async () => {
  // VueDatePicker에서 start/end 날짜 추출
  const [startDate, endDate] = date.value && Array.isArray(date.value)
      ? date.value.map(formatDate)
      : [tripDetail.value.startDate, tripDetail.value.endDate];

  // 멤버 상태 배열
  const updatedMembers = members.value.map(m => ({
    memberId: m.memberId,
    tripId: m.tripId,
    userId: m.userId,
    memberStatus: m.status
  }));

  const params = {
    tripId: tripDetail.value.tripId,
    tripName: newTitle.value,
    tripStatus: tripDetail.value.tripStatus,
    startDate: startDate,
    endDate: endDate,
    members: updatedMembers
  };
  console.log(params);

  try {
    await tripApi.updateTrip(params);
    alert("여행 정보가 성공적으로 업데이트되었습니다.");
  } catch (error) {
    console.error("업데이트 실패:", error);
    alert("업데이트 중 오류가 발생했습니다.");
  }
};

load();
</script>

<template>
  <Header :title="headerTitle" />
  <div class="content-container">
    <TravelCard2
      :tripName="tripDetail.tripName"
      :startDate="tripDetail.startDate"
      :endDate="tripDetail.endDate"
      :tripStatus="tripStatus"
      v-slot="{ activeTab }"
    >
      <div v-if="activeTab === '그룹 지출 내역'">지출 내역</div>
      <div v-else-if="activeTab === '선결제 내역'">선결제 내역</div>
      <div v-else>
        <label for="editName">여행 이름 수정</label><br>
        <input
            type="text"
            name="editName"
            id="editName"
            class="input-box"
            v-model="newTitle"
        >
        날짜 변경하기
        <VueDatePicker
            v-model="date"
            :range="{ noDisabledRange: true }"
            :enable-time-picker="false"
            :disabled-dates="disableDates"
            locale="ko"
            cancelText="취소"
            selectText="선택"
        />
        <p>멤버 목록</p>
        <!-- 멤버 목록 추가 -->
        <div class="member-list">
          <div class="member-list-item" v-for="member in members" :key="member.userId">
            <div class="avatar-and-name">
              <div class="avatar avatar-lg">{{member.name.charAt(0)}}</div>
              {{ member.name }}
            </div>
            <select
                class="status_selector"
                :value="member.status"
                :disabled="member.status === 'INVITED'"
                v-model="member.status"
            >
              <option v-if="member.status === 'INVITED'" value="INVITED">Invited</option>
              <option value="JOINED">Joined</option>
              <option value="LEFT">Left</option>
            </select>
          </div>
        </div>
        <button class="floating-pill-button" @click="handleUpdate">저장</button>
      </div>
    </TravelCard2>
  </div>
</template>

<style scoped>
/* 메인 콘텐츠 */
.content-container {
  flex-grow: 1;
  overflow-y: auto;
  padding: calc(56px) 1.25rem 1.25rem;
}

.member-list-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 5px 0;
}

.input-box {
  width: 100%;
  padding-left: 10px;
  padding-right: 50px;
  height: 40px;
  box-sizing: border-box;
  border-radius: 5px;
  border: 1px solid var(--theme-text-light);
  margin-bottom: 10px;
}

.avatar{
  border-radius: 50%;
  background: var(--theme-primary);
  color: white;
  display: flex;
  justify-content: center;
  align-items: center;
}

.avatar-lg{
  height : 35px;
  width : 35px;
  font-size : 14px;
  margin-right: 10px;
}

.avatar-and-name {
  display: flex;
  align-items: center;
}

.status_selector{
  height: 35px;
  padding: 5px 10px;
  font-size: 14px;
  border-color:  var(--theme-text-light);
  border-radius: 5px;
  color: var(--theme-text-light);
}

div{
  color: var(--theme-text);
}

.floating-pill-button {
  position: absolute;
  bottom: 20px;      /* 화면 하단에서 20px 띄우기 */
  right: 20px;       /* 화면 우측에서 20px 띄우기 */
  background-color: var(--theme-primary);
  color: white;
  border: none;
  border-radius: 50px;  /* 알약 모양 */
  padding: 12px 24px;   /* 위아래 12px, 좌우 24px */
  font-size: 16px;
  box-shadow: 0 4px 8px rgba(0,0,0,0.2);
  cursor: pointer;
  transition: background-color 0.3s ease;
  z-index: 1000;
}

.floating-pill-button:hover {
  background-color: var(--theme-primary-dark); /* 호버 시 조금 진한 색으로 변경 */
}
</style>
