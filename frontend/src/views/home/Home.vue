<script setup>
import { ref, onMounted, computed } from 'vue';
import { useRouter } from 'vue-router';
import { storeToRefs } from 'pinia';

import AccountCard from './AccountCard.vue';
import TravelInformationCard from './TravelInformationCard.vue';
import SettlementCard from './SettlementCard.vue';
import Footer from '@/components/layout/Footer.vue';

import tripApi from '@/api/tripApi.js';

import axios from 'axios';
import { useAuthStore } from '@/stores/authStore.js';
import { getMyUnsettledTrips } from '@/api/settlementApi.js';
import { getMyInfo } from '@/api/memberApi.js';
import {
  Bell,
  CalendarPlus,
  BellRing,
  PlaneTakeoff,
  Wallet,
} from 'lucide-vue-next';

const router = useRouter();

const userInfo = ref({ nickname: '', name: '' });
const ongoingTrips = ref([]);
const unsettledList = ref([]);

// API 호출
onMounted(async () => {
  try {
    const token = localStorage.getItem('accessToken');
    const res = await getMyInfo();

    // 유저 정보
    if (res?.success && res?.data) {
      userInfo.value = res.data;
      console.log('사용자 정보 설정 완료:', userInfo.value);
    } else {
      console.error('유저 정보 조회 실패:', res?.message || '데이터 없음');
    }

    // 여행 목록
    const tripRes = await tripApi.fetchTrips();
    console.log('받은 trip 목록:', tripRes);
    if (Array.isArray(tripRes)) {
      ongoingTrips.value = tripRes.filter(
        (trip) => trip.tripStatus === 'ACTIVE'
      );
    }

    // 미정산 내역
    const response = await getMyUnsettledTrips();
    unsettledList.value = response.data;
  } catch (err) {
    console.error('API 에러:', err);
    if (err.message?.includes('인증') || err.message?.includes('토큰')) {
      console.log('인증 오류로 로그인 페이지로 이동');
      router.push('/login');
    }
  }
});
const userNameInitial = computed(() => userInfo.value.name?.charAt(0) || '');

const goToNotification = () => router.push('/notification');
const goToGroupCreate = () => router.push('/trip/create');
const goToMyPage = () => router.push('/mypage');
</script>

<template>
  <div class="home-content">
    <div class="content">
      <!-- 헤더 -->
      <div class="header-section">
        <!-- 인사말 -->
        <div class="greeting-box">
          <span class="welcome">안녕하세요.</span>
          <span class="nickname" v-if="userInfo.nickname"
            >{{ userInfo.nickname }}님!</span
          >
        </div>
        <!-- 아이콘 -->
        <div class="icon-group">
          <div class="icon-btn" @click="goToNotification">
            <Bell class="header-icon" />
          </div>
          <div class="icon-btn" @click="goToGroupCreate">
            <CalendarPlus class="header-icon" />
          </div>
          <div class="icon-btn" @click="goToMyPage">
            <div class="profile-circle">
              {{ userNameInitial }}
            </div>
          </div>
        </div>
      </div>
      <!-- 메인 콘텐츠 -->
      <div class="main-section">
        <!-- 1. 정산 요청 -->
        <section v-if="unsettledList.length > 0" class="settlement-pending">
          <div class="section-header">
            <BellRing class="main-icon" />
            <span class="section-title">아직 안 한 정산</span>
          </div>
          <SettlementCard :settlements="unsettledList" />
        </section>
        <!-- 2. 진행 중인 여행 -->
        <section class="ongoing-trips">
          <div class="section-header">
            <PlaneTakeoff class="main-icon" />
            <span class="section-title">진행 중인 여행</span>
          </div>
          <TravelInformationCard
            v-for="trip in ongoingTrips"
            :key="trip.tripId"
            class="card"
            :trip="trip"
          />
        </section>
        <!-- 3. 내 계좌 요약 -->
        <section class="account-summary">
          <div class="section-header">
            <Wallet class="main-icon" />
            <span class="section-title">내 계좌</span>
          </div>
          <AccountCard
            class="card"
            v-if="userInfo.userId"
            :user-id="userInfo.userId"
          />
        </section>
      </div>
      <button class="floating-button" @click="goToGroupCreate">
        <span class="plus-icon">+</span> 새로운 여행
      </button>
    </div>
    <Footer class="footer" />
  </div>
</template>

<style scoped>
.home-content {
  margin-top: 8%;
  width: 100%;
  height: 100%;
  display: flex;
  flex-direction: column;
  background: #f8fafc;
  overflow: hidden;
  font-family: 'IBM Plex Sans KR', sans-serif;

  position: relative;
}

.content {
  flex: 1;
  overflow-y: auto;
  padding: 0px 32px 0px 32px;
  position: relative;
}

.header-section {
  width: 100%;
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 50px;
}
.greeting-box {
  display: flex;
  flex-direction: column;
  align-items: flex-start;
}

.nickname {
  font-size: 24px;
  font-weight: 700;
  color: #4a4a4a;
}

.welcome {
  font-size: 14px;
  color: #777;
}

.header-icon {
  display: flex;
  width: 35px;
  height: 32px;
  color: #4a4a4a;
}

.icon-group {
  display: flex;
  flex-direction: row;
  align-items: center;
  gap: 15px;
  cursor: pointer;
  transition: background-color 0.2s ease;
}

.profile-circle {
  width: 37px;
  height: 37px;
  border-radius: 50%;
  background-color: #fddf99;
  color: #333;
  display: flex;
  justify-content: center;
  align-items: center;
  font-weight: bold;
  font-size: 15px;
}

.main-section {
  width: 100%;
  display: flex;
  flex-direction: column;
  gap: 32px;
}

.section-title {
  font-size: 18px;
  font-weight: 700;
  margin-bottom: 12px;
  color: #333;
}

.section-header {
  display: flex;
  align-items: center;
  gap: 15px; /* 아이콘과 글자 사이 여백 */
  margin-bottom: 12px; /* 아래 내용과 간격 */
}

.main-icon {
  width: 24px;
  height: 24px;
  color: #4a4a4a;
  margin-bottom: 11px;
}

.card {
  width: 100%;
  display: flex;
  justify-content: center;
  margin-bottom: 24px;
}

.footer {
  margin-top: auto;
}

.floating-button {
  position: sticky;
  bottom: 18px;
  left: 600px;

  display: flex;
  align-items: center;
  justify-content: center;

  padding: 8px 18px;
  background-color: #ffe58a;
  border: none;
  border-radius: 999px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);

  font-size: 14px;
  font-weight: 600;
  color: #333;
  cursor: pointer;
  font-family: 'IBM Plex Sans KR', sans-serif;

  z-index: 10;
}

.floating-button:hover {
  background-color: #ffd940;
}

.plus-icon {
  font-size: 20px;
  margin-right: 6px;
}
</style>
