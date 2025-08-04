<script setup>
import Header from '@/components/layout/Header.vue';
import TravelCard2 from '@/components/common/TravelCard2.vue';
import tripApi from '@/api/tripApi.js';
import { ref } from 'vue';
import { useRoute } from 'vue-router';
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
const rawDate = ref({});

const load = async () => {
  const data = await tripApi.getTripDetail(route.params.tripId);
  if (!data) return;
  try {
    disableDates.value = await tripApi.getDisabledDates();
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
        <label for="editName">여행 이름 수정</label><br />
        <input type="text" name="editName" id="editName" class="input-box" />
        <p>날짜 변경하기</p>
        <VueDatePicker
          v-model="rawDate"
          :range="{ noDisabledRange: true }"
          :enable-time-picker="false"
          :disabled-dates="disableDates"
          locale="ko"
          cancelText="취소"
          selectText="선택"
        />
        <p>멤버 목록</p>
        <!-- 멤버 목록 추가 -->
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

.input-box {
  width: 100%;
  padding-left: 10px;
  padding-right: 50px;
  height: 40px;
  box-sizing: border-box;
  border-radius: 5px;
  border: 1px solid var(--theme-text-light);
}
</style>
