<script setup>
import { onMounted, ref } from 'vue';
import { useRouter } from 'vue-router';
import Footer from '@/components/layout/Footer.vue';
import {
  Briefcase,
  BriefcaseConveyorBelt,
  CircleUserRound,
  ChevronRight,
} from 'lucide-vue-next';
import { getMyInfo, logoutMember } from '@/api/memberApi.js';
import tripApi from "@/api/tripApi.js";

const router = useRouter();
const userInfo = ref({ nickname: '', name: '' });

// 마운트 시 사용자 정보 불러오기
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

    if (err.message?.includes('인증') || err.message?.includes('토큰')) {
      console.log('인증 오류로 로그인 페이지로 이동');
      router.push('/login');
    }
  }
});

// 페이지 이동
const goTo = (path) => router.push(path);

// ✅ 로그아웃 - memberApi의 logoutMember 함수 사용
const logout = async () => {
  try {
    console.log('로그아웃 시작');
    await logoutMember();

    // 토큰 정리
    localStorage.removeItem('accessToken');
    console.log('로그아웃 성공, 로그인 페이지로 이동');
    router.push('/login');
  } catch (e) {
    console.error('로그아웃 실패:', e);

    // 에러가 발생해도 토큰은 정리하고 로그인 페이지로 이동
    localStorage.removeItem('accessToken');
    router.push('/login');
  }
};

// 지난 여행, 예정된 여행
const readyTrips = ref([]);
const closedTrips = ref([]);

onMounted(async () => {
  readyTrips.value = await tripApi.getTripsByStatus('READY');
  closedTrips.value = await tripApi.getTripsByStatus('CLOSED');
})
</script>

<template>
  <div class="mypage-content">
    <div class="content">
      <!-- 상단 제목 -->
      <header class="header"><h1>마이페이지</h1></header>

      <!-- 프로필 이미지 + 닉네임 -->
      <div class="profile-section">
        <img
            src="@/assets/img/airplane_left.png"
            alt="프로필"
            class="profile-img"
        />
        <div class="nickname">{{ userInfo.nickname || '김냥이' }}</div>
      </div>

      <!-- 아이콘 -->
      <div class="icon-section">
        <div class="icon-wrapper" @click="goTo('/mypage/info')">
          <div class="icon-button"><CircleUserRound size="28" /></div>
          <span>회원 정보</span>
        </div>
        <div class="icon-wrapper" @click="goTo('/mypage/ready-trip')">
          <div class="icon-button"><Briefcase size="28" /></div>
          <span>예정된 여행</span>
        </div>
        <div class="icon-wrapper" @click="goTo('/mypage/closed-trip')">
          <div class="icon-button"><BriefcaseConveyorBelt size="28" /></div>
          <span>지난 여행</span>
        </div>
      </div>

      <!-- 메뉴 리스트 -->
      <div class="menu-list">
        <div class="menu-item" @click="goTo('/mypage/payment')">
          결제 수단 관리 <ChevronRight class="arrow" />
        </div>
        <div class="menu-item" @click="goTo('/mypage/faq')">
          공지사항 및 FAQ <ChevronRight class="arrow" />
        </div>
        <div class="menu-item" @click="goTo('/mypage/terms')">
          이용 약관 <ChevronRight class="arrow" />
        </div>
      </div>

      <!-- 로그아웃 -->
      <div class="logout" @click="logout">로그아웃</div>
    </div>

    <!-- 공통 푸터 -->
    <Footer class="footer" />
  </div>
</template>

<style scoped>
.mypage-content {
  width: 100%;
  height: 100%;
  background: #f8fafc;
  display: flex;
  flex-direction: column;
  overflow: hidden; /* 스크롤은 main-content에서 처리 */
}

.content {
  flex: 1;
  overflow-y: auto;
  padding: 0px 32px 0px 32px;
  min-height: 0;
}

.header {
  text-align: center;
  font-size: 20px;
  font-weight: bold;
  color: #333;
  margin-bottom: 32px;
}

.header h1 {
  font-size: 20px;
  margin-bottom: 16px;
  font-weight: bold;
  color: #333;
}

.profile-section {
  display: flex;
  flex-direction: row;
  align-items: center;
  justify-content: flex-start;
  margin: 36px 0 24px 0;
  padding-left: 8px;
  gap: 20px;
}

.profile-img {
  width: 70px;
  height: 70px;
  object-fit: contain;
  background-color: rgba(255, 209, 102, 0.65);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 10px;
}

.nickname {
  margin-top: 10px;
  font-size: 18px;
  font-weight: 700;
  color: #333;
}

.icon-section {
  display: flex;
  justify-content: center;
  gap: 48px;
  margin: 24px 0 32px;
  padding: 10px;
}

.icon-wrapper {
  display: flex;
  flex-direction: column;
  align-items: center;
  font-size: 13px;
  font-weight: 600;
  color: #444;
  cursor: pointer;
}

.icon-button {
  background-color: white;
  border: 1px solid #dcdcdc;
  border-radius: 50%;
  width: 54px;
  height: 54px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 8px;
}

.menu-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
  margin-bottom: 24px;
}

.menu-item {
  background: white;
  padding: 14px 20px;
  border-radius: 12px;
  font-size: 15px;
  font-weight: 600;
  color: #333;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.06);
  display: flex;
  justify-content: space-between;
  align-items: center;
  cursor: pointer;
}

.arrow {
  font-size: 18px;
  color: #bbb;
}

.logout {
  text-align: right;
  color: #ed5c5c;
  font-size: 13px;
  font-weight: 600;
  text-decoration: underline;
  cursor: pointer;
}

.footer {
  margin-top: auto;
}
</style>