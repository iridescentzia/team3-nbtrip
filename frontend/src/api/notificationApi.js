import api from '@/api';

// 유저 알림 조회
// export const fetchNotifications = (userId, category) => {
//   const categoryQuery = category && category !== 'ALL' ? `?category=${category}` : '';
//   return api.get(`/notifications/${userId}${categoryQuery}`);
// };

export const fetchNotifications = (category) => {
  const categoryQuery = category && category !== 'ALL' ? `?category=${category}` : '';
  return api.get(`/notifications${categoryQuery}`);
};

// 알림 읽음 처리
export const readNotification = (notificationId) => {
  return api.put(`/notifications/${notificationId}/read`);
};
