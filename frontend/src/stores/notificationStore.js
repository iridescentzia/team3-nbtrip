import { defineStore } from 'pinia';
import { fetchNotifications, readNotification } from '@/api/notificationApi';
// import { useAuthStore } from '@/stores/auth'; // 실제 적용 시 활성화

export const useNotificationStore = defineStore('notification', {
  state: () => ({
    notifications: [],
    selectedCategory: 'ALL'
  }),

  actions: {
    // 테스트
    async getNotifications(userId, category = 'ALL') {
      try {
        const { data } = await fetchNotifications(userId, category);
        this.notifications = data;
        this.selectedCategory = category;
      } catch (error) {
        console.error('알림 조회 실패:', error);
      }
    },

    // async getNotifications(category = 'ALL') {
    //   try {
    //     const authStore = useAuthStore(); // 로그인 유저 정보 가져오기
    //     const userId = authStore.user?.userId;

    //     if (!userId) {
    //       console.warn('로그인한 사용자 없음 - 알림 못 가져옴');
    //       return;
    //     }

    //     const { data } = await fetchNotifications(userId, category);
    //     this.notifications = data;
    //     this.selectedCategory = category;
    //   } catch (error) {
    //     console.error('알림 조회 실패:', error);
    //   }
    // },

    async readNotification(notificationId) {
      try {
        await readNotification(notificationId);
        const target = this.notifications.find(n => n.notificationId === notificationId);
        if (target) target.isRead = true; // UI 즉시 갱신
      } catch (error) {
        console.error('알림 읽음 처리 실패:', error);
      }
    }
  }
});
