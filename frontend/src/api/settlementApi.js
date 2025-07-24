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

/*
// [참고] 앞으로 2, 3단계 기능을 만들 때, 이 파일에 아래와 같이 함수를 추가해나갈 수 있습니다.
export const calculateOptimalSettlement = (tripId) => {
  return apiClient.get(`/settlements/${tripId}`);
};

export const requestSettlement = (tripId) => {
  return apiClient.post('/settlements', { tripId });
};
*/
