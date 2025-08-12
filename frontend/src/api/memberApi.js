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
            accountNumber: memberData.accountNumber,
            bankName: memberData.bankName,
            fcmToken: memberData.fcmToken || ''
        });
        return response.data;
    } catch (error) {
        console.error('회원가입 API 에러:', error);
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

// ======== 이메일 인증 API (JWT 토큰 기반) ========

// 이메일 인증번호 발송
export const sendVerificationEmail = async (email) => {
    try {
        const response = await apiClient.post('/auth/send-verification', { email });
        const responseData = response.data || response;

        const verificationToken = responseData.verificationToken ||
            responseData.token ||
            responseData.jwt ||
            responseData.accessToken ||
            responseData.data?.verificationToken ||
            responseData.data?.token ||
            response.headers?.authorization ||
            response.headers?.Authorization;

        const isSuccess = responseData.success !== false &&
            response.status >= 200 &&
            response.status < 300;

        return {
            success: isSuccess,
            verificationToken: verificationToken || 'fallback-token-' + Date.now(),
            message: responseData.message || '인증번호가 발송되었습니다.'
        };
    } catch (error) {
        console.error('❌ 이메일 발송 API 에러:', error);

        if (error.response?.status === 200 || error.response?.status === 201) {
            return {
                success: true,
                verificationToken: 'fallback-token-' + Date.now(),
                message: '인증번호가 발송되었습니다.'
            };
        }

        throw handleApiError(error, '이메일 인증번호 발송');
    }
};

// 이메일 인증번호 확인 - 토큰 없이도 동작하도록 수정
export const verifyEmailCode = async (verificationData) => {
    try {
        const requestData = {
            verificationToken: verificationData.verificationToken || '',
            code: verificationData.code
        };

        if (!verificationData.verificationToken || verificationData.verificationToken.startsWith('fallback-token-')) {
            requestData.email = verificationData.email;
        }

        const response = await apiClient.post('/auth/verify-email', requestData);
        const responseData = response.data || response;
        const isSuccess = responseData.success !== false &&
            response.status >= 200 &&
            response.status < 300;

        return {
            success: isSuccess,
            verified: responseData.verified || responseData.success || isSuccess,
            message: responseData.message || '이메일 인증이 완료되었습니다.'
        };
    } catch (error) {
        console.error('❌ 이메일 인증 확인 API 에러:', error);
        throw handleApiError(error, '이메일 인증번호 확인');
    }
};

// ====== Member API =====

// 닉네임 중복 확인 - 완전 수정
export const checkNicknameDuplicate = async (nickname) => {
    try {
        const response = await apiClient.post('/users/check-nickname', {
            nickname: nickname
        });

        return {
            success: true,
            exists: false,
            available: true,
            message: response.data.message || '사용 가능한 닉네임입니다.'
        };
    } catch (error) {

        // 409 CONFLICT - 중복된 닉네임
        if (error.response?.status === 409) {
            return {
                success: true,
                exists: true,
                available: false,
                message: error.response.data.message || '이미 사용 중인 닉네임입니다.'
            };
        }

        // 400 Bad Request - 유효성 검사 실패
        if (error.response?.status === 400) {
            return {
                success: false,
                exists: true,
                available: false,
                message: error.response.data.message || '올바르지 않은 닉네임 형식입니다.'
            };
        }

        // 500 Internal Server Error - 서버 오류
        if (error.response?.status === 500) {
            return {
                success: false,
                exists: true,
                available: false,
                message: '서버 오류가 발생했습니다. 잠시 후 다시 시도해주세요.'
            };
        }

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
    console.error(`❌ ${operation} 중 오류:`, error);

    if (error.response) {
        const { status, data } = error.response;

        switch (status) {
            case 400:
                return new Error(data.message || '잘못된 요청입니다.');
            case 401:
                return new Error(data.message || '인증이 필요하거나 토큰이 만료되었습니다.');
            case 403:
                return new Error(data.message || '접근 권한이 없습니다.');
            case 404:
                return new Error(data.message || '요청한 정보를 찾을 수 없습니다.');
            case 409:
                return new Error(data.message || '중복된 정보가 있습니다.');
            case 422:
                return new Error(data.message || '입력 데이터가 유효하지 않습니다.');
            case 429:
                return new Error('요청이 너무 많습니다. 잠시 후 다시 시도해주세요.');
            case 500:
                return new Error('서버에 일시적인 문제가 발생했습니다. 잠시 후 다시 시도해주세요.');
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

// 이메일 검증 유틸리티
export const isValidEmail = (email) => {
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    return emailRegex.test(email);
};

// 인증 코드 검증 유틸리티
export const isValidVerificationCode = (code) => {
    return /^\d{6}$/.test(code);
};

// 전체 내보내기
export default {
    // Auth
    registerMember,
    loginMember,
    logoutMember,

    // Email Verification
    sendVerificationEmail,
    verifyEmailCode,

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
    setStoredToken,
    isValidEmail,
    isValidVerificationCode
};
