import axios from 'axios';

const api = axios.create({
  // 필요시 baseURL 등 옵션 추가 가능
});

export default api;
