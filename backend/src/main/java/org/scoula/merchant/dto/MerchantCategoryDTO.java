package org.scoula.merchant.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.scoula.merchant.domain.MerchantCategoryVO;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MerchantCategoryDTO {
    private int categoryId;
    private String categoryName;

    public static MerchantCategoryDTO of(MerchantCategoryVO vo) {
        return MerchantCategoryDTO.builder()
                .categoryId(vo.getCategoryId())
                .categoryName(vo.getCategoryName())
                .build();
    }
}
