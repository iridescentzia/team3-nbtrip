package org.scoula.report.service;

import org.apache.ibatis.annotations.Param;
import org.scoula.report.dto.ChartDTO;
import org.scoula.report.dto.TripInfoDTO;

import java.util.List;

public interface ChartService {

    /**
     * 도넛 차트용 카테고리별 지출 금액 조회
     * @param tripId 조회할 여행 ID
     * @return 카테고리별 총 지출 리스트
     */
    List<ChartDTO> getDonutChart(Integer tripId);

    /**
     * 라인 차트용 날짜별 결제 건수·총 지출 조회
     * @param tripId 조회할 여행 ID
     * @return 날짜별 건수 및 총 지출 리스트
     */
    List<ChartDTO> getLineChart(Integer tripId);

    TripInfoDTO getTripInfo(Integer tripId);

    boolean isTripMember(int tripId, int userId);


}
