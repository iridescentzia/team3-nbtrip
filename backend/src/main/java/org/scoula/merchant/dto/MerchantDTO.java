package org.scoula.merchant.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.scoula.merchant.domain.MerchantVO;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MerchantDTO {
    private int categoryId;
    private String merchantName;
    private int sales;

    public static MerchantDTO of(MerchantVO vo) {
        return MerchantDTO.builder()
                .categoryId(vo.getCategoryId())
                .merchantName(vo.getMerchantName())
                .sales(vo.getSales())
                .build();
    }

    public MerchantVO toVO() {
        return MerchantVO.builder()
                .categoryId(categoryId)
                .merchantName(merchantName)
                .sales(sales)
                .build();
    }
}
