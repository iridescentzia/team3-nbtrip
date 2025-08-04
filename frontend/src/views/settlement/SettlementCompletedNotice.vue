<script setup>
import Header from "@/components/layout/Header2.vue";
import Button from "@/components/common/Button.vue";
import {useRoute, useRouter} from "vue-router";
import catImage from "@/assets/img/smiling_cat.png";
import axios from "axios";
import {ref} from "vue";

const router = useRouter()
const route = useRoute()
const tripId = route.params.tripId

const trip = ref({tripName: ''})

// 홈 화면으로 이동
const goToTravelReport = () => {
  router.push(`/report/${tripId}`)
}

const fetchTrip = async () => {
  try {
    const {data} = await axios.get(`/api/trips/${tripId}`)
    trip.value = data
  } catch (error) {
    console.error('여행 정보 불러오기 실패: ', error)
  }
}
</script>

<template>
  <div class="container">
    <Header title="정산하기" />
    <div class="content">
      <img class="cat-img" :src="catImage" alt="웃고있는 고양이"/>

      <div class="text-area">
        <p class="trip-name">{{trip.tripName}}</p>
        <h2 class="main-msg">정산이 완료되었습니다.</h2>
      </div>
    </div>

    <Button label="여행 리포트 보러 가기" @click="goToTravelReport"/>
  </div>
</template>

<style scoped>
.container {
  display: flex;
  flex-direction: column;
  height: 100vh;
  justify-content: space-between;
  padding: 16px;
}

.content {
  display: flex;
  flex-direction: column;
  align-items: center;
  margin-top: 32px;
}

.cat-img {
  width: 180px;
  margin-bottom: 24px;
}

.text-area {
  text-align: center;
}

.trip-name {
  color: #999;
  font-size: 14px;
  margin-bottom: 6px;
}

.main-msg {
  font-size: 20px;
  font-weight: bold;
  color: #333;
  margin-bottom: 6px;
}

.sub-msg {
  font-size: 14px;
  color: #666;
}
</style>