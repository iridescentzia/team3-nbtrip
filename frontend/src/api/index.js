import axios from 'axios';

const apiClient = axios.create({
  // 백엔드 API의 기본 주소를 입력합니다.
  // (예: http://localhost:8080/api)
  baseURL: 'http://localhost:8080/api',
  headers: {
    'Content-Type': 'application/json',
  },
});

// accessToken 자동 포함 인터셉터
apiClient.interceptors.request.use(config => {
  const token = localStorage.getItem('accessToken');  // 저장된 토큰 가져오기
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
}, error => {
  return Promise.reject(error);
});

export default apiClient;
