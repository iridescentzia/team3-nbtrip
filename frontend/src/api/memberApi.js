import axios from "axios";
import apiClient from "@/api/index.js";

// ======== Auth API ========

// 회원 가입
export const registerMember = async (memberData) => {
    try {
        const response = await apiClient.post('/auth/register', {
            email: memberData.email,
            password: memberData.password,
            passwordConfirm: memberData.passwordConfirm,
            nickname: memberData.nickname,
            name: memberData.name,
            phoneNumber: memberData.phoneNumber,
            fcmToken: memberData.fcmToken || ''
        });
        return response.data;
    } catch (error) {
        throw handleApiError(error, '회원가입');
    }
};

// 로그인
export const loginMember = async (loginData) => {
    try {
        const response = await apiClient.post('/auth/login', {
            email: loginData.email,
            password: loginData.password,
            fcmToken: loginData.fcmToken || ''
        });
        return response.data;
    } catch (error) {
        throw handleApiError(error, '로그인');
    }
};

// 로그아웃
export const logoutMember = async () => {
    try {
        const response = await apiClient.post('/auth/logout');
        return response.data;
    } catch (error) {
        throw handleApiError(error, '로그아웃');
    }
};

// ====== Member API =====

// 닉네임 중복 확인
export const checkNicknameDuplicate = async (nickname) => {
    try {
        const response = await apiClient.post('/users/check-nickname', {
            nickname
        });
        return response.data;
    } catch (error) {
        throw handleApiError(error, '닉네임 중복 확인');
    }
};

// 사용자 정보 조회
export const getUserInfo = async (userId) => {
    try {
        const response = await apiClient.get(`/users/${userId}`);
        return response.data;
    } catch (error) {
        throw handleApiError(error, '사용자 정보 조회');
    }
};

// FCM 토큰 갱신
export const updateFcmToken = async (fcmTokenData) => {
    try {
        const response = await apiClient.put('/users/fcm-token', {
            fcmToken: fcmTokenData.fcmToken
        });
        return response.data;
    } catch (error) {
        throw handleApiError(error, 'FCM 토큰 갱신');
    }
};

// ===== MyPage API =====

// 내 정보 조회 (JWT 기반)
export const getMyInfo = async () => {
    try {
        const response = await apiClient.get('/mypage');
        return response.data;
    } catch (error) {
        throw handleApiError(error, '내 정보 조회');
    }
};

// 비밀번호 확인 (회원 정보 수정 전 확인)
export const verifyPassword = async (password) => {
    try {
        const response = await apiClient.post('/mypage/verify-password', {
            currentPassword: password
        });
        return response.data;
    } catch (error) {
        throw handleApiError(error, '비밀번호 확인');
    }
};

// 회원정보 수정
// 회원정보 수정 함수 - 상세 디버깅 추가
export const updateMyInfo = async (memberData) => {
    try {
        const requestData = {
            nickname: memberData.nickname,
            name: memberData.name,
            phoneNumber: memberData.phoneNumber,
            email: memberData.email
        };

        if (memberData.password && memberData.password.trim() !== '') {
            requestData.password = memberData.password;
            console.log('비밀번호가 requestData에 포함됨');
        } else {
            console.log('비밀번호가 requestData에 포함되지 않음');
        }
        const response = await apiClient.put('/mypage', requestData);
        return response.data;
    } catch (error) {
        console.error('API 에러:', error.message);
        throw handleApiError(error, '회원정보 수정');
    }
};


// ====== 유틸리티 함수 ======
// API 에러 처리 함수
const handleApiError = (error, operation) => {
    console.error(`${operation} 중 오류:`, error);

    if (error.response) {
        const { status, data } = error.response;

        switch (status) {
            case 400:
                return new Error(data.message || '잘못된 요청입니다.');
            case 401:
                return new Error('인증이 필요하거나 토큰이 만료되었습니다.');
            case 403:
                return new Error('접근 권한이 없습니다.');
            case 404:
                return new Error('요청한 정보를 찾을 수 없습니다.');
            case 409:
                return new Error(data.message || '중복된 정보가 있습니다.');
            case 500:
                return new Error('서버 내부 오류가 발생했습니다.');
            default:
                return new Error(data.message || `${operation} 중 오류가 발생했습니다.`);
        }
    } else if (error.request) {
        return new Error('네트워크 연결을 확인해주세요.');
    } else {
        return new Error(`${operation} 중 예상치 못한 오류가 발생했습니다.`);
    }
};

// API 응답 성공 여부 확인
export const isApiSuccess = (response) => {
    return response && (response.success === true || response.status === 'success');
};

// 토큰 관련 유틸리티
export const clearTokens = () => {
    localStorage.removeItem('accessToken');
    localStorage.removeItem('refreshToken');
};

export const getStoredToken = () => {
    return localStorage.getItem('accessToken');
};

export const setStoredToken = (token) => {
    localStorage.setItem('accessToken', token);
};

export default {
    // Auth
    registerMember,
    loginMember,
    logoutMember,

    // Member
    getUserInfo,
    updateFcmToken,
    checkNicknameDuplicate,
    verifyPassword,

    // MyPage
    getMyInfo,
    updateMyInfo,

    // Utils
    isApiSuccess,
    clearTokens,
    getStoredToken,
    setStoredToken
};