// src/api/client.js
import axios from 'axios';

const apiClient = axios.create({
  // 배포 환경: Nginx가 /api -> 127.0.0.1:8080 으로 프록시
  // 로컬 개발: vite dev 서버 proxy('/api' -> http://localhost:8080) 사용
  baseURL: import.meta.env.VITE_API_BASE_URL || '/api',
  headers: { 'Content-Type': 'application/json' },
});

apiClient.interceptors.request.use(
    (config) => {
      const token = localStorage.getItem('accessToken');
      if (token) config.headers.Authorization = `Bearer ${token}`;
      return config;
    },
    (error) => Promise.reject(error)
);

export default apiClient;
