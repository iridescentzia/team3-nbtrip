package org.scoula.merchant.service;

import org.scoula.merchant.dto.MerchantAccountDTO;
import org.scoula.merchant.dto.MerchantCategoryDTO;
import org.scoula.merchant.dto.MerchantDTO;

public interface MerchantService {
    MerchantDTO getMerchant(int merchantId);

    MerchantCategoryDTO getMerchantCategory(int categoryId);

    MerchantAccountDTO getMerchantAccount(int merchantId);

    void createMerchant(MerchantDTO merchantDTO);
}
