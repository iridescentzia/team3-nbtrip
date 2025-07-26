package org.scoula.merchant.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.scoula.merchant.domain.MerchantAccountVO;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MerchantAccountDTO {
    private int merchantId;
    private String merchantBank;
    private String merchantAccountNumber;
    private int merchantBalance;

    public static MerchantAccountDTO of(MerchantAccountVO vo) {
        return MerchantAccountDTO.builder()
                .merchantId(vo.getMerchantId())
                .merchantBank(vo.getMerchantBank())
                .merchantAccountNumber(vo.getMerchantAccountNumber())
                .merchantBalance(vo.getMerchantBalance())
                .build();
    }
}
