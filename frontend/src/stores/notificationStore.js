import { defineStore } from 'pinia';
import { fetchNotifications, readNotification } from '@/api/notificationApi';

export const useNotificationStore = defineStore('notification', {
  state: () => ({
    notifications: [],
    selectedCategory: 'ALL'
  }),

  actions: {
    async getNotifications(userId, category = 'ALL') {
      try {
        const { data } = await fetchNotifications(userId, category);
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
