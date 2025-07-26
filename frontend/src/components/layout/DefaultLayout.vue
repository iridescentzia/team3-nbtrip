<script setup>
import Header from './Header.vue'
import Footer from './Footer.vue'
import Button from '../common/Button.vue'
import TravelCard from '../common/TravelCard.vue'

import {onMounted} from 'vue'
import {useTripStore} from "@/stores/trip.js";
import Summary from "@/components/common/Summary.vue";

const tripStore = useTripStore()

// 컴포넌트가 마운트될 때 trip 정보 가져오기
onMounted(()=>{
  tripStore.fetchActiveTrip()
})
</script>


<template>
  <div class="layout-wrapper">
    <div class="layout-container">
      <Header title="진행 중인 여행" />

      <main class="content">
        <slot />
      </main>
      <div class="mid">

      </div>
<!--      <Button />-->

      <Footer />
    </div>
  </div>
</template>

<style scoped>
.layout-wrapper {
  display: flex;
  justify-content: center;
  width: 100%;
  min-height: 100vh;
  background-color: #f9fafb; /* 기존 Tailwind 'bg-gray-50' */
  position: relative;
  overflow: hidden;

}

.content{
  padding-top: 56px;
  width: 100%;
  max-width: 414px;
  flex: 1;
  padding: 16px;
  box-sizing: border-box;
  //display: flex;
  flex-direction: column;
  align-items: center; /* 자식 요소들을 가운데 정렬 */
}

.layout-container {
  width: 100%;
  max-width: 414px; /* 모바일 기준 */
  position: relative;
  padding-top: 0px;
  margin-top:0px;
  //background: lightgray;
}

.mid{
  height:250px;
}
</style>
