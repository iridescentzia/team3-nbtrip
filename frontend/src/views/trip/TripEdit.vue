<script setup>
import memberApi from "@/api/memberApi.js";
import tripApi from "@/api/tripApi.js";
import { onMounted, ref} from "vue";
import {useRouter} from "vue-router";
import {usePaymentlistStore} from "@/stores/tripStore.js";

defineProps({
  isOwner: Boolean,
  isClosed: Boolean
})
const tripDetail = ref({
  tripName: '',
  startDate: '',
  endDate: '',
  amount: 0,
});
const tripStatus = ref('');
const router = useRouter();
const disableDates = ref([]);
const date = ref({});
const members = ref([]);
const newTitle = ref("");
const store = usePaymentlistStore();

const formatDate = (date) => {
  if (!date) return null;
  if (!(date instanceof Date)) {
    date = new Date(date);
  }
  const year = date.getFullYear();
  const month = String(date.getMonth() + 1).padStart(2, '0');
  const day = String(date.getDate()).padStart(2, '0');

  return `${year}-${month}-${day}`; // "YYYY-MM-DD"
};

const handleUpdate = async () => {
  // VueDatePicker에서 start/end 날짜 추출
  console.log("업데이트 버튼 클릭됨");
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
    // 아래 없어도 될까?
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

const load = async () => {
  const data = store.currentTrip;
  if (!data) return;
  tripDetail.value = {
    amount: 0,
    ...data
  };
  console.log(data);
  console.log("tripDetail: " + tripDetail.value);
  try {
    disableDates.value = await tripApi.getDisabledDates(store.currentTrip.tripId);
  } catch (e) {
    console.error('비활성화 날짜 불러오기 실패:', e);
  }
  newTitle.value = data.tripName;
  tripStatus.value = data.tripStatus;

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

  date.value = [store.currentTrip.startDate, store.currentTrip.endDate];

};

function toEditInvite(){
  router.push(`/trip/${store.currentTrip.tripId}/invite`);
}

defineExpose({
  handleUpdate
});

onMounted(async ()=>{
  await load();
})

</script>

<template>
  <label for="editName">여행 이름 수정</label><br>
  <input
      :disabled="!isOwner"
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
      :min-date="new Date()"
      locale="ko"
      cancelText="취소"
      selectText="선택"
  />
  <div class="list-title">
    <p>멤버 목록</p>
    <button
        class="to-edit-invite-btn"
        @click="toEditInvite"
        v-if="isOwner && !isClosed"
    >멤버 추가</button>
  </div>
  <!-- 멤버 목록 추가 -->
  <div class="member-list">
    <div class="member-list-item" v-for="member in members" :key="member.userId">
      <div class="avatar-and-name">
        <div class="avatar avatar-lg">{{member.nickname.charAt(0)}}</div>
        {{ member.nickname }}
      </div>
      <select
          class="status_selector"
          :value="member.status"
          :disabled="member.status === 'INVITED' || member.userId === store.currentTrip.ownerId || !isOwner "
          v-model="member.status"
      >
        <option v-if="member.status === 'INVITED'" value="INVITED">Invited</option>
        <option value="JOINED">Joined</option>
        <option value="LEFT">Left</option>
      </select>
    </div>
  </div>
</template>

<style scoped>
.list-title {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.to-edit-invite-btn {
  background: var(--theme-primary);
  color: var(--theme-text);
  border: none;
  border-radius: 5px;
  padding: 5px 10px;
  height: 50%;
  cursor: pointer;
}
.to-edit-invite-btn:hover {
  background: #ffd166;
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

</style>

<!--scoped에선 datepicker에 대한 커스터마이징이 먹히지 않아서 관련 내용 style에 정의-->
<style>
.dp__theme_light {
  --dp-primary-color: var(--theme-primary);
}
</style>
