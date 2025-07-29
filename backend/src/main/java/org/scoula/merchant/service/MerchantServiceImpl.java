package org.scoula.merchant.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.scoula.merchant.domain.MerchantCategoryVO;
import org.scoula.merchant.domain.MerchantVO;
import org.scoula.merchant.dto.MerchantCategoryDTO;
import org.scoula.merchant.dto.MerchantDTO;
import org.scoula.merchant.mapper.MerchantMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Log4j2
@Service
@RequiredArgsConstructor
@Transactional
public class MerchantServiceImpl implements MerchantService {
    final private MerchantMapper mapper;

    @Override
    public MerchantDTO getMerchant(int merchantId) {
        MerchantVO merchant = mapper.getMerchant(merchantId);
        return MerchantDTO.of(merchant);
    }

    @Override
    public MerchantCategoryDTO getMerchantCategory(int categoryId) {
        MerchantCategoryVO merchantCategory = mapper.getMerchantCategory(categoryId);
        return MerchantCategoryDTO.of(merchantCategory);
    }

    @Override
    public void createMerchant(MerchantDTO merchantDTO) {
        MerchantVO merchantVO = merchantDTO.toVO();

        mapper.createMerchant(merchantVO);
    }
}
