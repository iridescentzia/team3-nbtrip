<script setup>
import { House, Scan, Plane, User } from 'lucide-vue-next';
import { computed, ref } from 'vue';
import { useRoute, useRouter } from 'vue-router';

const router = useRouter();
const route = useRoute();

const navItems = [
  {
    name: 'home',
    label: '홈',
    icon: House,
    path: '/',
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
];

const activeTab = computed(() => {
  return route.path;
});

// State to track hovered tab
const hoveredTabName = ref(null);

const handlerTabClick = (item) => {
  if (route.path !== item.path) {
    router.push(item.path);
  }
};
</script>

<template>
  <footer class="footer">
    <div
      v-for="item in navItems"
      :key="item.name"
      class="footer-item"
      :class="{
        active:
          hoveredTabName === item.name ||
          (hoveredTabName === null && activeTab === item.path),
      }"
      @click="handlerTabClick(item)"
      @mouseenter="hoveredTabName = item.name"
      @mouseleave="hoveredTabName = null"
    >
      <div class="icon"><component :is="item.icon" /><br /></div>
      <span class="label">{{ item.label }}</span>
    </div>
  </footer>
</template>

<style scoped>
.footer {
  bottom: 0;
  left: 0;
  right: 0;
  margin: 0 auto;
  width: 100%;
  max-width: 375px;
  height: 69px;
  background-color: #ffffff;
  border-top: 1px solid #e2e8f0;
  display: flex;
  justify-content: space-around;
  align-items: center;
  font-family: 'IBM Plex Sans KR', sans-serif;
  z-index: 100;
  padding: 4px;
}

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
  transition: color 0.2s ease-in-out;
}

.footer-item .icon {
  width: 24px;
  height: 24px;
  color: #8d8d8d;
  display: block;
  transition: color 0.2s ease-in-out;
}

.footer-item.active {
  color: #fddf99;
  font-weight: 700;
}

.footer-item.active .icon {
  color: #fddf99;
}
</style>
