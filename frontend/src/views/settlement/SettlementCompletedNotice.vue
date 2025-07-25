<script setup>
import Header from "@/components/layout/Header.vue";
import Button from "@/components/common/Button.vue";
import {useRouter} from "vue-router";
import catImage from "@/assets/img/smiling_cat.png";

const router = useRouter()

// 홈 화면으로 이동
const goHome = () => {
  router.push('/')
}
</script>

<template>
  <div class="container">
    <Header title="정산하기" />
    <div class="content">
      <img class="cat-img" :src="catImage" alt="웃고있는 고양이"/>

      <div class="text-area">
        <p class="trip-name">서울 우정여행</p>
        <h2 class="main-msg">정산이 완료되었습니다.</h2>
      </div>
    </div>

    <Button label="여행 리포트 보러 가기" @click="goHome"/>
  </div>
</template>

<style scoped>
.container {
  display: flex;
  flex-direction: column;
  height: 100vh;
  justify-content: space-between;
  padding: 16px;
  background-color: #fefefe;
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