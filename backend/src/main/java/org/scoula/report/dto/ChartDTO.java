package org.scoula.report.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.scoula.report.domain.ChartVO;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChartDTO {

    private String category;

    private Double total_amount;

    private String date;

    private Long count;

    private String tripName;

    private String tripEnd;

    private String tripStart;

    /**
     * VO → DTO 변환
     */
    public static ChartDTO of(ChartVO vo) {
        if (vo == null) {
            return null;
        }
        return ChartDTO.builder()
                .category(vo.getCategory())
                .total_amount(vo.getTotal_amount())
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
                .total_amount(this.total_amount)
                .date(this.date)
                .count(this.count)
                .build();
    }
}

