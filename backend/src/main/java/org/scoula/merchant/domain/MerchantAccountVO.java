package org.scoula.merchant.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MerchantAccountVO {
    private int merchantAccountId;
    private int merchantId;
    private String merchantBank;
    private String merchantAccountNumber;
    private int merchantBalance;
}
