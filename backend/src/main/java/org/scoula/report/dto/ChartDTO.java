package org.scoula.report.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.scoula.report.domain.ChartVO;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ApiModel(description = "차트 데이터 전송 객체")
public class ChartDTO {

    @ApiModelProperty(value = "카테고리 이름 (도넛 차트)", example = "식음료")
    private String category;

    @ApiModelProperty(value = "카테고리별 총 지출 금액 (도넛·라인 차트)", example = "123456.78")
    private Double totalAmount;

    @ApiModelProperty(value = "날짜 (라인 차트, YYYY-MM-DD)", example = "2025-07-10")
    private String date;

    @ApiModelProperty(value = "해당 날짜의 결제 건수 (라인 차트)", example = "5")
    private Long count;

    /**
     * VO → DTO 변환
     */
    public static ChartDTO of(ChartVO vo) {
        if (vo == null) {
            return null;
        }
        return ChartDTO.builder()
                .category(vo.getCategory())
                .totalAmount(vo.getTotalAmount())
                .date(vo.getDate())
                .count(vo.getCount())
                .build();
    }

    /**
     * DTO → VO 변환
     */
    public ChartVO toVo() {
        return ChartVO.builder()
                .category(this.category)
                .totalAmount(this.totalAmount)
                .date(this.date)
                .count(this.count)
                .build();
    }
}

