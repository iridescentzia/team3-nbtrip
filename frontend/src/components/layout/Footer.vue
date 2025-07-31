<script setup>
import { House, Scan, Plane, User } from 'lucide-vue-next'
import { computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'

// vue router 인스턴스
const router = useRouter()
const route = useRoute()

// footer 탭 정보
const navItems = [
  {
    name: 'home',
    label: '홈',
    icon: House,
    path: '/home',
  },
  {
    name: 'trip',
    label: '여행',
    icon: Plane,
    path: '/trip',
  },
  {
    name: 'payment',
    label: '결제',
    icon: Scan,
    path: '/payment',
  },
  {
    name: 'mypage',
    label: '내 정보',
    icon: User,
    path: '/mypage',
  },
]

// 현재 활성화된 탭
const activeTab = computed(() => {
  return route.path
})

// 탭 클릭 시 실행되는 handler 함수
const handlerTabClick = (item) => {
  if (route.path !== item.path) {
    router.push(item.path)
  }
}
</script>

<template>
  <!-- 하단 고정 footer -->
  <footer class="footer">
    <div
      v-for="item in navItems"
      :key="item.name"
      class="footer-item"
      :class="{ active: activeTab === item.path }"
      @click="handlerTabClick(item)"
    >
      <div class="icon">
        <component :is="item.icon" /><br />
      </div>
      <span class="label">{{ item.label }}</span>
    </div>
  </footer>
</template>

<style scoped>
/* 하단 네비게이션 바 컨테이너 */
.footer {
  bottom: 0;
  left: 0;
  right: 0;
  margin: 0 auto;        /* PC에서 가운데 정렬 */
  width: 100%;
  max-width: 375px;      /* 모바일 최대 폭 */
  height: 69px;
  background-color: #ffffff;
  border-top: 1px solid #e2e8f0;
  display: flex;
  justify-content: space-around;
  align-items: center;
  font-family: 'IBM Plex Sans KR', sans-serif;
  z-index: 100;
}

/* 개별 탭 아이템 스타일 */
.footer-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  width: 60px;
  text-align: center;
  gap: 4px;
  font-size: 12px;
  color: #8d8d8d;
  font-weight: 400;
  white-space: nowrap;
  cursor: pointer;
}

/* 탭 내부 아이콘 영역 */
.footer-item .icon {
  width: 24px;
  height: 24px;
  color: #8d8d8d;
  display: block;
}

/* 활성화된 탭 스타일 */
.footer-item.active {
  color: #fddf99;
  font-weight: 700;
}

/* 활성화된 탭 아이콘 스타일 */
.footer-item.active .icon {
  color: #fddf99;
}
</style>
