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


    private Date payAt;
    private Integer amount;
    private Integer tripId;

    private String categoryName;
    private String categoryId;
    private String merchantId;

    private String category;

    private Double totalAmount;

    private String date;

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

