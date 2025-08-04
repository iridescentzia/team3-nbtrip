import apiClient from '@/api/index.js';

const BASE_URL = '/accounts';

export default {
    // 은행 목록 조회
    async getBankList() {
        const { data } = await apiClient.get(`${BASE_URL}/banks`);
        console.log('BANK LIST:', data);
        return data;
    },

    // 계좌 조회
    async getAccountByUserId(userId) {
        const { data } = await apiClient.get(`${BASE_URL}/${userId}`);
        console.log('ACCOUNT GET:', data);
        return data;
    },

    // 계좌 등록
    async registerAccount(accountRegisterDTO) {
        const { data } = await apiClient.post(`${BASE_URL}`, accountRegisterDTO);
        console.log('ACCOUNT REGISTER:', data);
        return data;
    },

    // 계좌 수정
    async updateAccount(userId, accountUpdateDTO) {
        const { data } = await apiClient.put(`${BASE_URL}/${userId}`, accountUpdateDTO);
        console.log('ACCOUNT UPDATE:', data);
        return data;
    }
}