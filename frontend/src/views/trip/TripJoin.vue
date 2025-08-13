<script setup>
import tripApi from "@/api/tripApi.js";
import memberApi from "@/api/memberApi.js";
import {ref} from 'vue';
import {useRouter, useRoute} from "vue-router";
import Button from "@/components/common/Button.vue";
import Header from "@/components/layout/Header.vue";
const router=useRouter();
const tripId = ref();
const tripDetails = ref({});
const ownerData = ref({});

const formatDate = (timestamp) => {
  return new Date(timestamp).toLocaleDateString('ko-KR', {
    timeZone: 'Asia/Seoul',
    year: 'numeric',
    month: '2-digit',
    day: '2-digit'
  }).replace(/\./g, '').replace(/\s/g, '-');
};

const load = async () => {
  const route = useRoute();
  tripId.value = route.params.tripId;
  try{
    tripDetails.value=await tripApi.getTripDetail(tripId.value);
    ownerData.value = await memberApi.getUserInfo(tripDetails.value.ownerId);
  }
  catch (e) {
    console.error("필요 데이터 로딩 중 오류 발생: "+e);
  }
}

const toNextPage = async () => {
  const available = await tripApi.isAvailableDate(tripDetails.value.startDate, tripDetails.value.endDate);
  console.log(available);
  // if (available) {
  if (true) {
    await tripApi.acceptInvitation(tripId.value);
    await router.replace(`/trip/join/${tripId.value}/complete`);
  } else {
    // 임시로 home으로 가도록 처리
    await router.push(`/`);
  }
}

load();
</script>

<template>
  <Header title="새로운 여행 만들기" @back="router.back"/>
  <div class="content-container">
    <img src="@/assets/img/airplane_right.png"/>
    <div class="trip-info" v-if="tripDetails && ownerData && tripDetails.members">
      <h2>[{{tripDetails.tripName}}]</h2>
      <div class="info-list">
        <div class="info-item">
          <span class="info-label">• 여행 기간:</span>
          <span class="info-content">{{formatDate(tripDetails.startDate)}} ~ {{formatDate(tripDetails.endDate)}}</span>
        </div>
        <div class="info-item">
          <span class="info-label">• 여행 인원:</span>
          <span class="info-content">{{tripDetails.members.length}}명</span>
        </div>
        <div class="info-item">
          <span class="info-label">• 그룹장:</span>
          <span class="info-content">{{ownerData.name}}</span>
        </div>
      </div>
    </div>
    <h3>참여하시겠습니까?</h3>
    <Button class="next-btn" @click="toNextPage" label="참여하기"/>
  </div>
</template>

<style scoped>
img{
  height: 30vh;
}
/* 메인 콘텐츠 */
.content-container {
  flex-grow: 1;
  overflow-y: auto;
  padding: calc(56px) 1.25rem 1.25rem;
  display: flex;
  flex-direction: column;
  align-items: center;
}

.trip-info {
  display: flex;
  flex-direction: column;
  align-items: center;
  margin-bottom: 2rem;
}

.trip-info h2 {
  margin-bottom: 1rem;
  text-align: center;
}

.info-list {
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
  width: 100%;
  max-width: 300px;
}

.info-item {
  display: flex;
  align-items: flex-start;
  gap: 0.5rem;
}

.info-label {
  flex-shrink: 0;
  width: 80px; /* 라벨 영역 고정 너비 */
  font-weight: 500;
  color: #666;
}

.info-content {
  flex: 1;
  color: #333;
  font-weight: 600;
}

.next-btn {
  width: 90%;
  height: 50px;
  position: absolute;
  bottom: 0;
  left: 50%;
  transform: translateX(-50%);
}

h3 {
  margin-top: 1rem;
  margin-bottom: 2rem;
  text-align: center;
}
</style>