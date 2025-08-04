import { defineStore } from 'pinia';
import { fetchNotifications, readNotification } from '@/api/notificationApi';

export const useNotificationStore = defineStore('notification', {
  state: () => ({
    notifications: [],
    selectedCategory: 'ALL'
  }),

  actions: {
    // 테스트
    // async getNotifications(userId, category = 'ALL') {
    //   try {
    //     const res = await fetchNotifications(userId, category);
    //     console.log('받은 데이터:', res.data); // 삭제
    //     this.notifications = res.data;
    //     this.selectedCategory = category;
    //   } catch (error) {
    //     console.error('알림 조회 실패:', error);
    //   }
    // },

    // 토큰 기반 알림 조회
    async getNotifications(category = 'ALL') {
      try {
        const { data } = await fetchNotifications(category);
        this.notifications = data;
        this.selectedCategory = category;
      } catch (error) {
        console.error('알림 조회 실패:', error);
      }
    },

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