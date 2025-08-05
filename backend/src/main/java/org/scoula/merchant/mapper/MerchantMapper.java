package org.scoula.merchant.mapper;

import org.apache.ibatis.annotations.Param;
import org.scoula.merchant.domain.MerchantCategoryVO;
import org.scoula.merchant.domain.MerchantVO;

import java.util.List;

public interface MerchantMapper {
    MerchantVO getMerchant(int id);

    MerchantCategoryVO getMerchantCategory(int categoryId);

    void createMerchant(MerchantVO merchant);

    // 사업자 매출 증가
    int increaseSales(@Param("merchant_id") int merchantId, @Param("amount") int amount);

    // 전체 카테고리 목록 조회
    List<MerchantCategoryVO> getAllMerchantCategories();
}
