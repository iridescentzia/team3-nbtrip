<script setup>
import tripApi from "@/api/tripApi.js";
import {ref} from 'vue';
import {useRouter, useRoute} from "vue-router";
import Button from "@/components/common/Button.vue";
import Header2 from "@/components/layout/Header2.vue";
const router=useRouter();
const tripId = ref();
const tripDetails = ref({});


const load = async () => {
  const route = useRoute();
  tripId.value = route.params.tripId;
  try{
    tripDetails.value=await tripApi.getTripDetail(tripId.value);
  }
  catch (e) {
    console.error("필요 데이터 로딩 중 오류 발생: "+e);
  }
}

const toNextPage=() => {
  router.push(`/`);
}

load();
</script>

<template>
  <Header2 title="그룹 참여"/>
  <div class="content-container">
    <img src="@/assets/img/smiling_cat.png"/>
    <div class="trip-info" v-if="tripDetails">
      <h2>[{{tripDetails.tripName}}]의<br>
        멤버가 되셨어요!
      </h2>
    </div>
    <p>여행 정보를 확인해보세요!</p>
    <Button class="next-btn" @click="toNextPage" label="여행 정보로 이동하기"/>
  </div>
</template>

<style scoped>
img{
  height: 30vh;
}

.trip-create-view {
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
.trip-create-view {
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
  display: flex;
  flex-direction: column;
  align-items: center;
}

.trip-info {
  display: flex;
  flex-direction: column;
  align-items: center;
}


.next-btn {
  width: 90%;
  height: 50px;
  position: absolute;
  bottom : 10%;
  left: 50%;
  transform: translateX(-50%);
}
</style>