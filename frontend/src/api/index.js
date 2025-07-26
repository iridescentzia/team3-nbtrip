import axios from 'axios';

// axios 인스턴스 생성
const api = axios.create({
  baseURL: '/', // 필요에 따라 API 기본 경로 설정
  headers: { 'Content-Type': 'application/json' },
});

export default api;
