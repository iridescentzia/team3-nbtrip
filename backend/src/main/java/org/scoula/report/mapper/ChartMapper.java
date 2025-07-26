package org.scoula.report.mapper;

import org.apache.ibatis.annotations.Param;
import org.mapstruct.Mapper;
import org.scoula.report.domain.ChartVO;
import java.util.List;


@Mapper
public interface ChartMapper {

    /**
     * 도넛 차트용: 카테고리별 총 지출 금액 조회
     * @param tripId 조회할 여행 ID
     */
    List<ChartVO> selectDonutChart(@Param("tripId")String tripId);

    /**
     * 라인 차트용: 날짜별 결제 건수 및 총 지출 조회
     * @param tripId 조회할 여행 ID
     */
    List<ChartVO> selectLineChart(@Param("tripId")String tripId);

    String getTripStatus(@Param("tripId")String tripId,
                         @Param("trip_status")String tripStatus);
}