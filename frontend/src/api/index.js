import axios from 'axios';

const apiClient = axios.create({
  // 백엔드 API의 기본 주소를 입력합니다.
  // (예: http://localhost:8080/api)
  baseURL: 'http://localhost:8080/api',
  headers: {
    'Content-Type': 'application/json',
  },
});

/*
// [참고] 로그인 기능 구현 후, 아래 주석을 해제하여 모든 요청에 JWT 토큰을 자동으로 추가할 수 있습니다.
apiClient.interceptors.request.use(config => {
  const token = localStorage.getItem('accessToken'); // 예시: 로컬 스토리지에서 토큰 가져오기
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
}, error => {
  return Promise.reject(error);
});
*/

export default apiClient;
