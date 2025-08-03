<script setup>
import Header from "@/components/layout/Header.vue";
import TravelCard2 from "@/components/common/TravelCard2.vue";
import tripApi from "@/api/tripApi.js";
import {ref} from "vue";
import {useRoute} from "vue-router";
import VueDatePicker from '@vuepic/vue-datepicker';

const tripDetail = ref({
  tripName: '',
  startDate: '',
  endDate: ''
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
  <div class="view-wrapper">
    <div class="trip-detail-view">
      <Header :title="headerTitle" />
      <div class="content-container">
        <TravelCard2
            :tripName="tripDetail.tripName"
            :startDate="tripDetail.startDate"
            :endDate="tripDetail.endDate"
            v-slot="{ activeTab }"
        >
          <div v-if="activeTab === '그룹 지출 내역'">
            지출 내역
          </div>
          <div v-else-if="activeTab === '선결제 내역'">
            선결제 내역
          </div>
          <div v-else>
            <label for="editName">여행 이름 수정</label><br>
            <input type="text" name="editName" id="editName" class="input-box">
            <p>날짜 변경하기</p>
            <VueDatePicker
                inline auto-apply
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
    </div>
  </div>
</template>

<style scoped>
.trip-detail-view {
  --theme-primary: rgba(255, 209, 102, 0.65);
  --theme-primary-dark: #e2c05e;
  --theme-bg: #f8f9fa;
  --theme-text: #333333;
  --theme-text-light: #888888;
}

/* 화면 중앙 정렬을 위한 wrapper 스타일 */
.view-wrapper {
  display: flex;
  justify-content: center;
  width: 100%;
  min-height: 100vh; /* 화면 전체 높이를 차지하도록 */
  background-color: #ffffff;
  padding: 2rem 0; /* 위아래 여백 추가 */
}

/* 전체 레이아웃 */
.trip-detail-view {
  z-index: 1;
  width: 100%;
  max-width: 24rem; /* 384px */
  background-color: var(--theme-bg);
  display: flex;
  flex-direction: column;
  border-radius: 1.5rem; /* 둥근 모서리 */
  box-shadow: 0 25px 50px -12px rgb(0 0 0 / 0.25); /* 그림자 효과 */
  overflow: hidden; /* 둥근 모서리 적용을 위해 */
  position: relative; /* Header 컴포넌트의 fixed 포지션 기준점 */
  height: 844px; /* 특정 스마트폰 높이를 기준으로 고정 */
  max-height: 90vh; /* 화면 높이의 90%를 넘지 않도록 설정 */
}

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
  border: 1px solid var(--theme-text-light)
}


</style>