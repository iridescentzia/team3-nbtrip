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

    //payment 테이블
    private Date payAt; //결제일시
    private Integer amount; //결제금액
    private Integer tripId; //여행 ID

    //join으로 가져올 값
    private String categoryName; //카테고리 종류
    private String categoryId;   //카테고리 ID
    private String merchantId;   //가맹점 ID
    private String tripStatus;   //여행 상태

    /** 카테고리 이름 (도넛 차트) */
    private String category;

    /** 카테고리별 총 지출 금액 (도넛 차트 / 라인 차트의 총지출) */
    private Double totalAmount;

    /** "YYYY-MM-DD" 형식의 날짜 (라인 차트) */
    private String date;

    /** 해당 날짜의 결제 건수 (라인 차트) */
    private Long count;
}
