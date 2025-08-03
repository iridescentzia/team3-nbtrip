<script setup>
import { ref, onMounted } from "vue";
import AccountCard from './AccountCard.vue'
import TravelInformationCard from './TravelInformationCard.vue'
import tripApi from "@/api/tripApi.js";
import { useRouter } from "vue-router";
import {getMyInfo} from '@/api/memberApi.js'
import Footer from "@/components/layout/Footer.vue";
import { 
  Bell, 
  CalendarPlus, 
  BellRing,
  PlaneTakeoff,
  Wallet,
} from "lucide-vue-next";



const userInfo = ref({ userId: null, nickname: '', name: '' });
const router = useRouter();
const ongoingTrips = ref([]);


// API 호출 (닉네임)
onMounted(async () => {
  try {
    console.log('마운트 시작');

    // ✅ getMyInfo() 사용 (userId 파라미터 불필요)
    const userRes = await getMyInfo();
    console.log('응답 결과:', userRes);
    // ✅ 응답 구조에 맞게 수정
    if (userRes?.success && userRes?.data) {
      userInfo.value = userRes.data;
      console.log('사용자 정보 설정 완료:', userInfo.value);
    } else {
      console.error('유저 정보 조회 실패:', userRes?.message || '데이터 없음');
    }
  

    // 여행 목록 가져오기
    const tripRes = await tripApi.fetchTrips();
    if (Array.isArray(tripRes)) {
      console.log('전체 여행 목록:', tripRes);

      // 진행 중인 여행 필터링 (ACTIVE + 내가 참여한 여행)
      ongoingTrips.value = tripRes.filter(trip =>{
          console.log('🔍 trip.tripStatus:', trip.tripStatus);
          const isActive = trip.tripStatus === 'ACTIVE';
          console.log(`[${trip.tripName}] isActive: ${isActive}`);
          return isActive
      });

      console.log('진행 중인 여행:', ongoingTrips.value);
    } else {
      console.error('여행 목록 조회 실패:', tripRes?.message || '데이터 없음');
    }

  } catch (err) {
    console.error('API 에러:', err);
  }
});

const goToNotification = () => router.push("/notification");
// const goToGroupCreate = () => router.push("/groupcreate");
const goToMyPage = () => router.push("/mypage");

</script>



<template>
  <div class="home-content">

    <div class="content">
      <!-- 헤더 -->
      <div class="header-section">
        <!-- 인사말 -->
        <div class="greeting-box">
          <span class="welcome">안녕하세요.</span>
          <span class="nickname">{{ userInfo.nickname || '김냥이' }}님!</span>
        </div>
        <!-- 아이콘 -->
        <div class="icon-group">
          <div class="icon-btn" @click="goToNotification">
            <Bell class="header-icon" />
          </div>
          <div class="icon-btn">
          <!-- <button class="icon-btn" @click="goToGroupCreate"> -->
            <CalendarPlus class="header-icon" />
          </div>
          <div class="icon-btn" @click="goToMyPage">
            <div class="profile-circle">
              {{ userInfo.name?.charAt(0) || '?' }}
            </div>
          </div>
        </div>
      </div>
      <!-- 메인 콘텐츠 -->
      <div class="main-section">
        <!-- 1. 정산 요청 -->
        <section class="settlement-pending">
          <div class="section-header">
            <BellRing class="main-icon"/>
            <span class="section-title">아직 안 한 정산</span>
          </div>
          
        </section>
        <!-- 2. 진행 중인 여행 -->
        <section class="ongoing-trips">
          <div class="section-header">
            <PlaneTakeoff class="main-icon"/>
            <span class="section-title">진행 중인 여행</span>
          </div>
          <TravelInformationCard
            v-for="trip in ongoingTrips"
            :key="trip.tripId"
            class="card"
            :tripId="trip.tripId"
          />
        </section>
        <!-- 3. 내 계좌 요약 -->
        <section class="account-summary">
          <div class="section-header">
            <Wallet class="main-icon"/>
            <span class="section-title">내 계좌</span>
          </div>
          <AccountCard class="card"
            bankName="카카오뱅크"
            accountNumber="111111111"
            :balance="1000000"
          />
        </section>
      </div>
    </div>
    
    <Footer class="footer"/>
  </div>
</template>

<style scoped>
.home-content {
  width: 100%;
  height: 100%;
  display: flex;
  flex-direction: column;
  background: #f8fafc;
  overflow: hidden;
  font-family: 'IBM Plex Sans KR', sans-serif;
}

.content {
  flex: 1;
  overflow-y: auto;
  padding: 0px 32px 0px 32px;
  min-height: 0;
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
  color: #4A4A4A;
}

.welcome {
  font-size: 14px;
  color: #777;
}


.header-icon {
  display: flex;
  width: 35px;
  height: 32px;
  color: #4A4A4A;
}

.icon-group {
  display: flex;
  flex-direction: row; /* 아이콘 가로 정렬 */
  align-items: center;
  gap: 15px; /* 아이콘 사이 간격 */
}


.profile-circle {
  width: 37px;
  height: 37px;
  border-radius: 50%;
  background-color: #FDDF99;
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

.main-icon{
  width: 24px;
  height: 24px;
  color: #4A4A4A;
  margin-bottom: 11px;
}

.card{
  width: 100%;
  display: flex;
  justify-content: center;
  margin-bottom: 24px;
}

.footer {
  margin-top: auto;
}

</style>