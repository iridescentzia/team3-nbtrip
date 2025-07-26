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
<!--  <DefaultLayout>-->
<!--    <RouterView />-->
<!--  </DefaultLayout>-->
  <PaymentList></PaymentList>
<!--  <div>연동 테스트 중</div>-->
</template>

<style scoped>
</style>