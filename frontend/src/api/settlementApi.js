// src/api/settlementApi.js

import apiClient from './index';

/**
 * 정산 1단계: 정산 요약 정보 조회 API
 * @param {number} tripId - 여행 ID
 * @returns {Promise} - Axios Promise 객체
 */
export const getSettlementSummary = (tripId) => {
  // 1단계에서 만든 백엔드 API 주소를 호출합니다.
  return apiClient.get(`/settlements/${tripId}/summary`);
};

/**
 * 정산 2단계: 최종 정산 결과 계산 API
 * @param {number} tripId - 여행 ID
 */
export const calculateFinalSettlement = (tripId) => {
  return apiClient.get(`/settlements/${tripId}/calculate`);
};

/**
 *  정산 3단계: 정산 요청 생성 API
 * @param {object} payload - { tripId }
 */
export const requestSettlement = (payload) => {
  return apiClient.post('/settlements', payload);
};

/**
 * 개인별 정산 내역 조회 API
 * @param {number} tripId - 여행 ID
 * @return {Promise} - Axios Promise
 */
export const getMySettlementDetails = (tripId) => {
  return apiClient.get(`/settlements/my/${tripId}`);
};

/**
 * 여행별 정산 결과 조회 API (그룹장용)
 * @param {number} tripId - 여행 ID
 */
export const getSettlementsByTripId = (tripId) => {
  return apiClient.get(`/settlements/${tripId}`);
};

/**
 * 송금 처리 API
 * @param {object} payload - { settlementIds: [...] }
 */
export const transferMoney = (payload) => {
  return apiClient.post('/settlements/transfer', payload);
};

/**
 * 여행 미정산 존재 여부 조회
 * @param {number} tripId - 여행 ID
 */
export const getRemainingSettlements = (tripId) => {
  return apiClient.get(`/settlements/${tripId}/remaining`);
};

/**
 * 사용자 전체 정산 상태 조회
 */
export const getMySettlementStatus = () => {
  return apiClient.get('/settlements/status');
};

/**
 * 정산 요청 알림 발송 API
 * @param {number} tripId - 여행 ID
 * @returns {Promise} - Axios Promise 객체
 */
export const sendSettlementNotification = (tripId) => {
  return apiClient.post(`/settlements/${tripId}/notify`);
};
