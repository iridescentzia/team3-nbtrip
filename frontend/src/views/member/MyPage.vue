<script setup>

import Header from "@/components/layout/Header.vue";
import Footer from "@/components/layout/Footer.vue";
import catImage from "@/assets/img/airplane_left.png";
import {CircleUserRound, Briefcase, BriefcaseConveyorBelt, ChevronRight} from "lucide-vue-next";
import {useRouter, useRoute} from "vue-router";

const router = useRouter()

const goTo = (target) => {
  router.push(`/${target}`)
}

const logout = () => {
  console.log('로그아웃')
}

</script>

<template>
  <Header title="마이페이지" />

  <!-- 프로필 -->
  <div class="profile-section">
    <img class="profile-img" :src="catImage" alt="프로필">
    <p class="nickname">김냥이</p>
  </div>

  <!-- 아이콘 메뉴 -->
  <div class="icon-menu">
    <div class="icon-item">
      <div class="icon-circle">
        <CircleUserRound size="24" />
      </div>
      <p>회원 정보</p>
    </div>
    <div class="icon-item">
      <div class="icon-circle">
        <Briefcase size="24" />
      </div>
      <p>예정된 여행</p>
    </div>
    <div class="icon-item">
      <div class="icon-circle">
        <BriefcaseConveyorBelt size="24" />
      </div>
      <p>지난 여행</p>
    </div>
  </div>

  <!-- 카드 형태 메뉴 -->
  <div class="menu-cards">
    <div class="menu-card" @click="goTo('payment')">
      <span>결제 수단 관리</span>
      <ChevronRight size="20" color="#666" />
    </div>
    <div class="menu-card" @click="goTo('faq')">
      <span>공지사항 및 FAQ</span>
      <ChevronRight size="20" color="#666" />
    </div>
    <div class="menu-card" @click="goTo('terms')">
      <span>이용 약관</span>
      <ChevronRight size="20" color="#666" />
    </div>
  </div>

  <!-- 로그아웃 -->
  <p class="logout" @click="logout">로그아웃</p>

  <Footer></Footer>
</template>

<style scoped>
.mypage-container {
  padding: 16px;
  text-align: center;
}

.title {
  font-size: 18px;
  font-weight: bold;
  margin-bottom: 16px;
}

.profile-section {
  display: flex;
  flex-direction: row;
  align-items: center;
  padding: 20px;
  position: relative;
  height: 120px;
}

.profile-img {
  width: 80px;
  height: 80px;
  border-radius: 50%;
  background-color: #FFE4B5;
  padding: 8px;
  flex-shrink: 0;
  position: absolute;
  left: 60px;
  top: 50%;
  transform: translateY(-50%);
  object-fit: contain;
  box-sizing: border-box;
}

.nickname {
  font-size: 20px;
  font-weight: bold;
  margin: 0;
  position: absolute;
  left: 50%;
  top: 50%;
  transform: translate(-50%, -50%);
}

.icon-menu {
  display: flex;
  justify-content: flex-start;
  margin: 32px 20px;
  padding: 0;
  position: relative;
  height: 80px;
  width: calc(100% - 40px);
}

.icon-item {
  text-align: center;
  cursor: pointer;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
  position: absolute;
}

.icon-item:nth-child(1) {
  left: 40px;
}

.icon-item:nth-child(2) {
  left: 50%;
  transform: translateX(-50%);
}

.icon-item:nth-child(3) {
  right: 40px;
}

.icon-circle {
  width: 56px;
  height: 56px;
  border-radius: 50%;
  background-color: #ffffff;
  border: 1px solid #e9ecef;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.2s ease;
}

.icon-circle:hover {
  background-color: #e9ecef;
}

.icon-item p {
  font-size: 12px;
  color: #666;
  margin: 0;
}

.menu-cards {
  margin: 20px;
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.menu-card {
  background-color: white;
  border-radius: 12px;
  padding: 20px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
  border: 1px solid #f1f3f4;
  cursor: pointer;
  display: flex;
  justify-content: space-between;
  align-items: center;
  transition: all 0.2s ease;
}

.menu-card:hover {
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  transform: translateY(-1px);
}

.menu-card span {
  font-size: 16px;
  color: #333;
  font-weight: 500;
}

.logout {
  color: #ED7B73;
  font-weight: 500;
  margin: 20px;
  text-align: right;
  cursor: pointer;
  font-size: 14px;
}

.logout:hover {
  color: #ff3742;
}
</style>
