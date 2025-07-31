<script setup>
import { RouterLink, RouterView } from 'vue-router'
import DefaultLayout from './components/layout/DefaultLayout.vue'
import PaymentList from "@/views/paymentlist/PaymentList.vue";
import axios from 'axios'
import {onMounted} from 'vue'

onMounted(()=>{
  axios.get('/api/ping')
      .then(res => console.log("응답:", res.data))
      .catch(err => console.error("에러:", err))
})
</script>

<template>
  <!-- 
    여기에 Header.vue 컴포넌트를 배치하면, 
    모든 페이지에 공통 헤더가 보이게 됩니다. 
  -->
  <!-- <Header title="N빵트립" :show-back-button="false" /> -->

  <!-- 
    이곳이 바로 Vue Router가 주소에 맞는 페이지를 
    보여주는 핵심적인 공간입니다. 
  -->
  <RouterView />

  <!-- 
    여기에 Footer.vue 컴포넌트를 배치하면, 
    모든 페이지에 공통 푸터가 보이게 됩니다. 
  -->
</template>

<style scoped></style>
