<script setup>
import { RouterLink, RouterView } from 'vue-router';
import DefaultLayout from './components/layout/DefaultLayout.vue';
import PaymentList from '@/views/paymentlist/PaymentList.vue';
import axios from 'axios';

import { onMounted, ref } from 'vue';
import { useRouter } from 'vue-router';

import { getMyInfo, logoutMember } from '@/api/memberApi.js';

const router = useRouter();
const userInfo = ref({ nickname: '', name: '' });

onMounted(async () => {
  try {
    console.log('마이페이지 마운트 시작');

    // ✅ getMyInfo() 사용 (userId 파라미터 불필요)
    const res = await getMyInfo();
    console.log('응답 결과:', res);

    // ✅ 응답 구조에 맞게 수정
    if (res?.success && res?.data) {
      userInfo.value = res.data;
      console.log('사용자 정보 설정 완료:', userInfo.value);
    } else {
      console.error('유저 정보 조회 실패:', res?.message || '데이터 없음');
    }
  } catch (err) {
    console.error('마이페이지 API 에러:', err);

    if (
      err.message?.includes('인증') ||
      err.message?.includes('토큰') ||
      err.message?.includes('로그인')
    ) {
      console.log('인증 오류로 로그인 페이지로 이동');
      router.push('/login');
    }
  }
});
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
