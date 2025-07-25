package org.scoula.report.mapper;

import org.scoula.report.domain.ChartVO;
import java.util.List;

public interface ChartMapper {

    /**
     * 도넛 차트용: 카테고리별 총 지출 금액 조회
     * @param tripId 조회할 여행 ID
     */
    List<ChartVO> selectDonutChart(Long tripId);

    /**
     * 라인 차트용: 날짜별 결제 건수 및 총 지출 조회
     * @param tripId 조회할 여행 ID
     */
    List<ChartVO> selectLineChart(Long tripId);

    String getTripStatus(Long tripId);
}