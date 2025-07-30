<script setup>
import { onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import Footer from '@/components/layout/Footer.vue'
import { Briefcase, BriefcaseConveyorBelt, CircleUserRound } from 'lucide-vue-next'
import axios from 'axios'

const router = useRouter()
const userInfo = ref({ nickname: '', name: '' })

// 마운트 시 내 정보 불러오기
onMounted(async () => {
  try {
    const res = await axios.get('/api/mypage', {
      headers: {
        Authorization: `Bearer ${localStorage.getItem('accessToken')}`
      }
    })

    if (res.data.success) {
      userInfo.value = res.data.data
    } else {
      console.error('유저 정보 조회 실패:', res.data.message)
    }
  } catch (err) {
    console.error('마이페이지 에러:', err)
  }
})

// 페이지 이동
const goTo = (path) => router.push(path)

// 로그아웃
const logout = async () => {
  try {
    await axios.post('/api/auth/logout', {}, {
      headers: {
        Authorization: `Bearer ${localStorage.getItem('accessToken')}`
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
          결제 수단 관리 <span class="arrow">›</span>
        </div>
        <div class="menu-item" @click="goTo('/faq')">
          공지사항 및 FAQ <span class="arrow">›</span>
        </div>
        <div class="menu-item" @click="goTo('/terms')">
          이용 약관 <span class="arrow">›</span>
        </div>
      </div>

      <!-- 로그아웃 -->
      <div class="logout" @click="logout">로그아웃</div>
    </div>

    <!-- 공통 푸터 -->
    <Footer />
  </div>
</template>

<style scoped>
.mypage-wrapper {
  width: 384px;
  height: 800px;
  background: #f8fafc;
  margin: 0 auto;
  margin-bottom: 100px;
  padding: 32px 24px;
  box-sizing: border-box;
  border-radius: 24px;
  outline: 1px solid black;
  outline-offset: -1px;
  box-shadow: 0px 25px 50px -12px rgba(0, 0, 0, 0.25);

  display: flex;
  flex-direction: column;
  position: relative;
}

.content {
  flex-grow: 1;
  display: flex;
  flex-direction: column;
  min-height: 0;
  padding-bottom: 40px;
}

.header {
  text-align: center;
  font-size: 20px;
  font-weight: 800;
  color: #4a4a4a;
  margin-bottom: 16px;
}

.profile-section {
  display: flex;
  flex-direction: column;
  align-items: center;
  margin: 16px 0 32px;
}

.profile-img {
  width: 120px;
  height: 120px;
  object-fit: contain;
  background: none;
  border: none;
  border-radius: 0;
  box-shadow: none;
}

.nickname {
  margin-top: 12px;
  font-size: 24px;
  font-weight: 800;
  color: #333;
}

.icon-section {
  display: flex;
  justify-content: space-around;
  margin: 20px 0;
}

.icon-wrapper {
  display: flex;
  flex-direction: column;
  align-items: center;
  font-size: 14px;
  font-weight: 700;
  color: #4a4a4a;
  cursor: pointer;
}

.icon-button {
  background-color: white;
  border: 1px solid #8d8d8d;
  border-radius: 50%;
  width: 60px;
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 6px;
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
  font-size: 16px;
  font-weight: 700;
  color: #4a4a4a;
  box-shadow: 0 1px 2px rgba(0, 0, 0, 0.05);
  display: flex;
  justify-content: space-between;
  align-items: center;
  cursor: pointer;
}

.arrow {
  font-size: 20px;
  color: #888;
}

.logout {
  text-align: right;
  color: #ed7b73;
  font-size: 14px;
  font-weight: 700;
  text-decoration: underline;
  cursor: pointer;
}
</style>
