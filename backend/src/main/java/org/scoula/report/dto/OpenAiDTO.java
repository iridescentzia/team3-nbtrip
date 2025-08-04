package org.scoula.report.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.scoula.report.domain.OpenAiVO;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OpenAiDTO {

    private String category;

    private int total_amount;

    private String date;


    /**
     * VO → DTO 변환
     */
    public static OpenAiDTO of(OpenAiVO vo) {
        if (vo == null) {
            return null;
        }
        return OpenAiDTO.builder()
                .category(vo.getCategory())
                .total_amount(vo.getTotal_amount())
                .date(vo.getDate())
                .build();
    }

    /**
     * DTO → VO 변환
     */
    public OpenAiVO toVo() {
        return OpenAiVO.builder()
                .category(this.category)
                .total_amount(this.total_amount)
                .date(this.date)
                .build();
    }
}

