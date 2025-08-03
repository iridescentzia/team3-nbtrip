package org.scoula.report.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * 차트에 사용할 도메인 VO.
 *  - 도넛 차트: category, totalAmount 사용
 *  - 라인 차트: date, count, totalAmount 사용
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChartVO {

    /** 카테고리 이름 (도넛 차트) */
    private String category;

    /** 카테고리별 총 지출 금액 (도넛 차트 / 라인 차트의 총지출) */
    private Double total_amount;

    /** "YYYY-MM-DD" 형식의 날짜 (라인 차트) */
    private String date;

    /** 해당 날짜의 결제 건수 (라인 차트) */
    private Long count;

    /** 여행 이름  */
    private String tripName;
    /** 여행 시작 날짜 */
    private String tripEnd;
    /** 여행 마지막 날짜 */
    private String tripStart;

}
