import { defineStore } from 'pinia';
import { fetchNotifications, readNotification } from '@/api/notificationApi';

export const useNotificationStore = defineStore('notification', {
  state: () => ({
    notifications: [],
    selectedCategory: 'ALL'
  }),

  actions: {
    // 토큰 기반 알림 조회
    async getNotifications(category = 'ALL') {
      try {
        const { data } = await fetchNotifications();
        console.log('🔍 data:', data);

        if (category === 'ALL'){
          this.notifications = data;
        } else if(category === 'SETTLEMENT') {
          this.notifications = data.filter(n =>
            ['SETTLEMENT', 'REMINDER', 'COMPLETED'].includes(n.notificationType)
          );
        } else if (category === 'INVITE') {
          this.notifications = data.filter(n => 
            n.notificationType === 'INVITE' &&
            ['JOINED', 'LEFT', null].includes(n.memberStatus));
        }else{
          this.notifications = data.filter(n => n.notificationType === category);
        }

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