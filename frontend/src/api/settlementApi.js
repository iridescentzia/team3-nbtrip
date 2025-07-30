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
 *  정산 2단계: 정산 요청 생성 API
 * @param {object} payload - { tripId }
 */
export const requestSettlement = (payload) => {
  return apiClient.post('/settlements', payload);
};

/**
 * 정산 3단계: 개인화된 정산 내역 조회 API
 * @param {number} tripId - 여행 ID
 * @returns {Promise} - Axios Promise 객체
 */
export const getMySettlementDetails = (tripId) => {
  // 백엔드 컨트롤러에 새로 만든 주소를 호출합니다.
  // userId는 JWT 토큰을 통해 서버에서 자동으로 인식하므로 보낼 필요가 없습니다.
  return apiClient.get(`/settlements/my/${tripId}`);
};

/**
 *  정산 3단계: 송금 처리 API
 * @param {object} payload - { settlementIds: [...] }
 */
export const transferMoney = (payload) => {
  return apiClient.post('/settlements/transfer', payload);
};
