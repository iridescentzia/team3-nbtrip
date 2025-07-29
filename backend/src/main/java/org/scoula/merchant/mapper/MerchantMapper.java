package org.scoula.merchant.mapper;

import org.scoula.merchant.domain.MerchantCategoryVO;
import org.scoula.merchant.domain.MerchantVO;

public interface MerchantMapper {
    MerchantVO getMerchant(int id);

    MerchantCategoryVO getMerchantCategory(int categoryId);

    void createMerchant(MerchantVO merchant);
}
