package org.scoula.merchant.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MerchantVO {
    private int merchantId;
    private int categoryId;
    private String merchantName;
    private int sales;
}
