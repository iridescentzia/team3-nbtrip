package org.scoula.merchant.service;

import org.scoula.merchant.dto.MerchantCategoryDTO;
import org.scoula.merchant.dto.MerchantDTO;

public interface MerchantService {
    MerchantDTO getMerchant(int merchantId);

    MerchantCategoryDTO getMerchantCategory(int categoryId);

    void createMerchant(MerchantDTO merchantDTO);

    // 사업자 매출 증가
    void increaseSales(int merchantId, int amount);
}
