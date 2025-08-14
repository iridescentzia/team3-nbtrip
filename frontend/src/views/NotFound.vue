<script setup>
import { useRoute } from 'vue-router';
import Button from '@/components/common/Button.vue';
import router from '@/router';

const route = useRoute();

console.log(route.params);
</script>

<template>
  <div class="center-content">
    <img src="@/assets/img/crying_cat.png" alt="우는 고양이" />
    <h2>존재하지 않는 페이지입니다.</h2>
  </div>

  <footer class="footer">
    <Button label="홈으로 돌아가기" @click="router.push('/')" />
  </footer>
</template>

<style scoped>
.center-content {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 80vh; /* 화면 높이의 70% 정도로 설정 */
  text-align: center;
}
.center-content img {
  max-width: 300px;
  margin-bottom: 20px;
}
</style>
