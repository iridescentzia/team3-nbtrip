<script setup>
import { ref, onMounted, computed } from 'vue';
import { useRouter } from 'vue-router';
import { storeToRefs } from 'pinia';
import { useNotificationStore } from '@/stores/notificationStore';

import AccountCard from './AccountCard.vue';
import TravelInformationCard from './TravelInformationCard.vue';
import SettlementCard from './SettlementCard.vue';
import Footer from '@/components/layout/Footer.vue';
import AdCard from './AdCard.vue';

import tripApi from '@/api/tripApi.js';

import axios from 'axios';
import { useAuthStore } from '@/stores/authStore.js';
import { getMyUnsettledTrips } from '@/api/settlementApi.js';
import { getMyInfo } from '@/api/memberApi.js';
import { Bell, BellRing, PlaneTakeoff, Wallet } from 'lucide-vue-next';

const router = useRouter();
const notificationStore = useNotificationStore();
const { unreadCount } = storeToRefs(notificationStore);
const displayUnreadCount = computed(() => {
  if (unreadCount.value > 99) return '99+';
  return unreadCount.value;
});

const userInfo = ref({ nickname: '', name: '' });
const ongoingTrips = ref([]);
const unsettledList = ref([]);

const adTrips = ref([
  {
    id: 1,
    title: '지금 가장 힙한 도시',
    city: '서울',
    description:
      '요즘 가장 트렌디한 도시, 서울. 눈부신 야경과 세련된 카페, 세계적인 브랜드가 모인 쇼핑 거리까지, 도시 전체가 볼거리로 가득합니다.',
    imageUrl:
      'https://img.freepik.com/free-photo/seoul-cityscape-twilight-south-korea_335224-273.jpg?semt=ais_hybrid&w=740&q=80',
    tags: ['활기찬', '맛집 탐방', '쇼핑 천국'],
  },
  {
    id: 2,
    title: '바다와 도시의 조화',
    city: '부산',
    description:
      '푸른 바다와 화려한 도시가 어우러진 부산. 해변에서의 여유와 도시의 활기를 동시에 느낄 수 있는 매력적인 곳입니다.',
    imageUrl:
      'https://www.visitbusan.net/uploadImgs/files/hqimgfiles/20200327141200390_thumbL',
    tags: ['해변', '야경', '신선한 해산물'],
  },
  {
    id: 3,
    title: '자연 속 힐링 여행',
    city: '제주',
    description:
      '에메랄드빛 바다와 아름다운 자연 경관이 펼쳐지는 제주. 일상에서 벗어나 진정한 힐링을 경험하고 싶다면 제주로 떠나보세요.',
    imageUrl:
      'https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcTPzYIAFb3tn9OuTi1UjA8sARrSkE98rduq_Q&s',
    tags: ['자연', '휴양', '오름 트레킹'],
  },
]);

// Ad Carousel Drag Logic
const adCarouselTrack = ref(null);
const isDragging = ref(false);
const startX = ref(0);
const scrollLeft = ref(0);

const onMouseDown = (e) => {
  isDragging.value = true;
  startX.value = e.pageX - adCarouselTrack.value.offsetLeft;
  scrollLeft.value = adCarouselTrack.value.scrollLeft;
  adCarouselTrack.value.style.cursor = 'grabbing';
};

const onMouseLeave = () => {
  isDragging.value = false;
  adCarouselTrack.value.style.cursor = 'grab';
};

const onMouseUp = () => {
  isDragging.value = false;
  adCarouselTrack.value.style.cursor = 'grab';
};

const onMouseMove = (e) => {
  if (!isDragging.value) return;
  e.preventDefault();
  const x = e.pageX - adCarouselTrack.value.offsetLeft;
  const walk = (x - startX.value) * 2; // Drag speed
  adCarouselTrack.value.scrollLeft = scrollLeft.value - walk;
};

// API 호출
onMounted(async () => {
  notificationStore.getNotifications();
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
const userNameInitial = computed(
  () => userInfo.value.nickname?.charAt(0) || ''
);

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
            <span v-if="unreadCount > 0" class="badge-dot">{{
              displayUnreadCount
            }}</span>
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

          <template v-if="ongoingTrips.length > 0">
            <TravelInformationCard
              v-for="trip in ongoingTrips"
              :key="trip.tripId"
              class="card"
              :trip="trip"
            />
          </template>
          <TravelInformationCard v-else class="card" :empty="true" />
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

        <!-- Advertisement Section -->
        <section class="ad-section">
          <h2 class="ad-title">이런 국내 여행지는 어떠세요?</h2>
          <div
            class="ad-carousel-wrapper"
            ref="adCarouselTrack"
            @mousedown="onMouseDown"
            @mouseleave="onMouseLeave"
            @mouseup="onMouseUp"
            @mousemove="onMouseMove"
          >
            <AdCard v-for="trip in adTrips" :key="trip.id" :trip="trip" />
          </div>
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
  display: flex;
  flex-direction: column;
  flex: 1;
  overflow-y: auto;
  padding: 0px 28px 0px 28px;
  position: relative;
}

.header-section {
  width: 100%;
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 40px;
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
  transition: transform 0.3s ease-in-out;
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
  color: #f8fafc;
  display: flex;
  justify-content: center;
  align-items: center;
  font-weight: bold;
  font-size: 15px;
  transition: all 0.2s ease-in-out;
}

.profile-circle:hover {
  transform: scale(1.1);
  box-shadow: 0 0 0 3px rgba(253, 223, 153, 0.7);
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
  margin-bottom: 6px; /* 아래 내용과 간격 */
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
  margin-bottom: 15px;
}

.footer {
  margin-top: auto;
}

.floating-button {
  position: sticky;
  bottom: 18px;

  display: flex;
  align-items: center;
  justify-content: center;

  padding: 8px 18px;
  margin-top: auto;
  align-self: flex-end;
  background-color: #ffe58a;
  border: none;
  border-radius: 999px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);

  font-size: 14px;
  font-weight: 600;
  color: #333;
  cursor: pointer;
  font-family: 'IBM Plex Sans KR', sans-serif;
  transition: all 0.2s ease-in-out;
  z-index: 10;
}

.floating-button:hover {
  background-color: #ffd940;
  transform: scale(1.03);
}

.plus-icon {
  font-size: 20px;
  margin-right: 6px;
}

.badge-dot {
  position: absolute;
  top: -4px;
  right: -4px;
  background-color: red;
  color: white;
  font-size: 10px;
  font-weight: bold;
  padding: 2px 3px;
  border-radius: 999px;
  min-width: 16px;
  text-align: center;
  line-height: 1;
}
.icon-btn {
  position: relative;
}

.icon-btn:hover .header-icon {
  transform: rotate(-10deg);
}

.ad-section {
  margin-top: 0px;
}

.ad-title {
  font-size: 18px;
  font-weight: 700;
  color: #333;
  margin-bottom: 16px;
  margin-top: 0px;
}

.ad-carousel-wrapper {
  display: flex;
  overflow-x: scroll;
  cursor: grab;
  padding-top: 10px;
  padding-bottom: 10px;
  margin-top: -10px;
  margin-bottom: -10px;
  -ms-overflow-style: none; /* IE and Edge */
  scrollbar-width: none; /* Firefox */
}
.ad-carousel-wrapper::-webkit-scrollbar {
  display: none; /* Chrome, Safari and Opera */
}

.ad-carousel-wrapper > * {
  margin-right: 16px;
}
.ad-carousel-wrapper > *:last-child {
  margin-right: 0;
}
</style>
