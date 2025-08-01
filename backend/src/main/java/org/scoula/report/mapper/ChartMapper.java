package org.scoula.report.mapper;

import org.apache.ibatis.annotations.Param;
import org.mapstruct.Mapper;
import org.scoula.report.domain.ChartVO;
import org.scoula.report.dto.TripInfoDTO;

import java.util.List;


@Mapper
public interface ChartMapper {

    /**
     * 도넛 차트용: 카테고리별 총 지출 금액 조회
     * @param tripId 조회할 여행 ID
     */
    List<ChartVO> selectDonutChart(@Param("tripId")Integer tripId);

    /**
     * 라인 차트용: 날짜별 결제 건수 및 총 지출 조회
     * @param tripId 조회할 여행 ID
     */
    List<ChartVO> selectLineChart(@Param("tripId")Integer tripId);

    TripInfoDTO selectTripInfo(@Param("tripId")Integer tripId);

}