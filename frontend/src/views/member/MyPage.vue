<script setup>
import { onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import Footer from '@/components/layout/Footer.vue'
import { Briefcase, BriefcaseConveyorBelt, CircleUserRound, ChevronRight } from 'lucide-vue-next'
import { getMyInfo, logoutMember } from "@/api/memberApi.js";
import Header2 from "@/components/layout/Header2.vue";

const router = useRouter()
const userInfo = ref({ nickname: '', name: '' })

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
const goTo = (path) => router.push(path)

// ✅ 로그아웃 - memberApi의 logoutMember 함수 사용
const logout = async () => {
  try {
    console.log('로그아웃 시작');
    await logoutMember();

    // 토큰 정리
    localStorage.removeItem('accessToken');
    localStorage.removeItem('refreshToken');

    console.log('로그아웃 성공, 로그인 페이지로 이동');
    router.push('/login');
  } catch (e) {
    console.error('로그아웃 실패:', e);

    // 에러가 발생해도 토큰은 정리하고 로그인 페이지로 이동
    localStorage.removeItem('accessToken');
    localStorage.removeItem('refreshToken');
    router.push('/login');
  }
}
</script>

<template>
  <div class="mypage-wrapper">
    <div class="content">
      <!-- 상단 제목 -->
      <Header2 title="마이페이지"></Header2>

      <!-- 프로필 이미지 + 닉네임 -->
      <div class="profile-section">
        <img src="@/assets/img/airplane_left.png" alt="프로필" class="profile-img" />
        <div class="nickname">{{ userInfo.nickname || '김냥이' }}</div>
      </div>

      <!-- 아이콘 -->
      <div class="icon-section">
        <div class="icon-wrapper" @click="goTo('/mypage/info')">
          <div class="icon-button"><CircleUserRound size="28" /></div>
          <span>회원 정보</span>
        </div>
        <!--      <div class="icon-wrapper" @click="goTo('/trips?status=ready')">-->
        <div class="icon-wrapper">
          <div class="icon-button"><Briefcase size="28" /></div>
          <span>예정된 여행</span>
        </div>
        <!--      <div class="icon-wrapper" @click="goTo('/trips?status=closed')">-->
        <div class="icon-wrapper">
          <div class="icon-button"><BriefcaseConveyorBelt size="28" /></div>
          <span>지난 여행</span>
        </div>
      </div>

      <!-- 메뉴 리스트 -->
      <div class="menu-list">
        <div class="menu-item" @click="goTo('/mypage/payment')">
          결제 수단 관리 <ChevronRight class="arrow"/>
        </div>
        <div class="menu-item" @click="goTo('/mypage/faq')">
          공지사항 및 FAQ <ChevronRight class="arrow"/>
        </div>
        <div class="menu-item" @click="goTo('/mypage/terms')">
          이용 약관 <ChevronRight class="arrow"/>
        </div>
      </div>

      <!-- 로그아웃 -->
      <div class="logout" @click="logout">로그아웃</div>
    </div>

    <!-- 공통 푸터 -->
    <Footer class="footer"/>
  </div>
</template>

<style scoped>
.mypage-wrapper {
  --theme-primary: rgba(255, 209, 102, 0.65);
  --theme-bg: #f8f9fa;
  --theme-text: #333333;
  --theme-text-light: #888888;

  width: 100%;
  max-width: 24rem;
  height: 844px;
  margin: 0 auto;
  background-color: var(--theme-bg);
  border-radius: 1.5rem;
  box-shadow: 0 25px 50px -12px rgb(0 0 0 / 0.25);
  display: flex;
  flex-direction: column;
  overflow: hidden;
  position: relative;
  box-sizing: border-box;
}

.content {
  flex: 1;
  display: flex;
  flex-direction: column;
  padding: 1.25rem;
  padding-top: calc(56px + 1.25rem);
  overflow-y: auto;
}

.profile-section {
  display: flex;
  align-items: center;
  gap: 1rem;
  margin-bottom: 2rem;
  padding-left: 1rem;
}

.profile-img {
  width: 70px;
  height: 70px;
  background-color: var(--theme-primary);
  border-radius: 9999px;
  display: flex;
  align-items: center;
  justify-content: center;
  object-fit: contain;
  padding: 10px;
}

.nickname {
  font-size: 1.125rem;
  font-weight: 700;
  color: var(--theme-text);
}

.icon-section {
  display: flex;
  justify-content: center;
  gap: 50px;
  margin-bottom: 2rem;
  padding: 0 0.5rem;
}

.icon-wrapper {
  display: flex;
  flex-direction: column;
  align-items: center;
  font-size: 0.8125rem;
  font-weight: 600;
  color: var(--theme-text);
  cursor: pointer;
}

.icon-button {
  background-color: white;
  border: 1px solid #e5e7eb;
  border-radius: 9999px;
  width: 54px;
  height: 54px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 0.5rem;
  box-shadow: 0 1px 2px rgb(0 0 0 / 0.05);
}

.menu-list {
  display: flex;
  flex-direction: column;
  gap: 0.75rem;
  margin-bottom: 2rem;
}

.menu-item {
  background-color: white;
  padding: 0.875rem 1.25rem;
  border-radius: 0.75rem;
  font-size: 0.9375rem;
  font-weight: 600;
  color: var(--theme-text);
  box-shadow: 0 1px 3px rgb(0 0 0 / 0.06);
  display: flex;
  justify-content: space-between;
  align-items: center;
  cursor: pointer;
}

.arrow {
  font-size: 1.125rem;
  color: #bbbbbb;
}

.logout {
  text-align: right;
  color: #ed5c5c;
  font-size: 0.8125rem;
  font-weight: 600;
  text-decoration: underline;
  cursor: pointer;
  margin-top: auto;
  padding-bottom: 1rem;
}

.footer {
  padding: 1rem 0;
  background-color: var(--theme-bg);
}
</style>