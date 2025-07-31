<script setup>
import { onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import Footer from '@/components/layout/Footer.vue'
import { Briefcase, BriefcaseConveyorBelt, CircleUserRound, ChevronRight } from 'lucide-vue-next'
import axios from 'axios'

const router = useRouter()
const userInfo = ref({ nickname: '', name: '' })

// 마운트 시 사용자 정보 불러오기
onMounted(async () => {
  const token = localStorage.getItem('accessToken')
  if (!token) {
    console.error('Access Token이 없습니다. 로그인 페이지로 이동합니다.')
    router.push('/login')
    return
  }
  try {
    const res = await axios.get('/api/mypage', {
      headers: {
        Authorization: `Bearer ${token}`
      }
    })
    console.log('응답 결과:', res.data)
    if (res.data?.success && res.data?.data) {
      userInfo.value = res.data.data
    } else {
      console.error('유저 정보 조회 실패:', res.data?.message || '데이터 없음')
    }
  } catch (err) {
    console.error('마이페이지 API 에러:', err)
  }
})

// 페이지 이동
const goTo = (path) => router.push(path)

// 로그아웃
const logout = async () => {
  try {
    const token = localStorage.getItem('accessToken')
    await axios.post('/api/auth/logout', {}, {
      headers: {
        Authorization: `Bearer ${token}`
      }
    })
    localStorage.removeItem('accessToken')
    router.push('/login')
  } catch (e) {
    console.error('로그아웃 실패:', e)
  }
}
</script>

<template>
  <div class="mypage-wrapper">
    <div class="content">
      <!-- 상단 제목 -->
      <header class="header"><h1>마이페이지</h1></header>

      <!-- 프로필 이미지 + 닉네임 -->
      <div class="profile-section">
        <img src="@/assets/img/airplane_left.png" alt="프로필" class="profile-img" />
        <div class="nickname">{{ userInfo.nickname || '김냥이' }}</div>
      </div>

      <!-- 아이콘 -->
      <div class="icon-section">
        <div class="icon-wrapper" @click="goTo('/my/info')">
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
        <div class="menu-item" @click="goTo('/my/payment')">
          결제 수단 관리 <ChevronRight class="arrow"/>
        </div>
        <div class="menu-item" @click="goTo('/faq')">
          공지사항 및 FAQ <ChevronRight class="arrow"/>
        </div>
        <div class="menu-item" @click="goTo('/terms')">
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
  width: 384px;
  height: 800px;
  position: relative;
  background: #f8fafc;
  box-shadow: 0px 25px 50px -12px rgba(0, 0, 0, 0.25);
  overflow: auto;
  border-radius: 24px;
  outline: 1px black solid;
  outline-offset: -1px;
  margin: 0 auto;
  padding: 32px 32px 0;
  box-sizing: border-box;
  display: flex;
  flex-direction: column;
}

.content {
  flex: 1;
  display: flex;
  flex-direction: column;
  min-height: 0;
}

.header {
  text-align: center;
  font-size: 20px;
  font-weight: bold;
  color: #333;
  margin-bottom: 16px;
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
  margin: 12px 0 24px;
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
  padding: 12px 0 16px;
  margin-top: auto;
}
</style>
