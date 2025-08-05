package org.scoula.merchant.service;

import org.scoula.merchant.dto.MerchantCategoryDTO;
import org.scoula.merchant.dto.MerchantDTO;

import java.util.List;

public interface MerchantService {
    MerchantDTO getMerchant(int merchantId);

    MerchantCategoryDTO getMerchantCategory(int categoryId);

    void createMerchant(MerchantDTO merchantDTO);

    // 사업자 매출 증가
    void increaseSales(int merchantId, int amount);

    // 전체 카테고리 목록 조회
    public List<MerchantCategoryDTO> getAllMerchantCategories();
}
