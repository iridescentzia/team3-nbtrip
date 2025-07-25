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
