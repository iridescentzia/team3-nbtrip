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

import java.util.List;
import java.util.stream.Collectors;

@Log4j2
@Service
@RequiredArgsConstructor
@Transactional
public class MerchantServiceImpl implements MerchantService {
    final private MerchantMapper mapper;
    private final MerchantMapper merchantMapper;

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

    // 사업자 매출 증가
    @Override
    public void increaseSales(int merchantId, int amount) {
        MerchantVO merchatVO = mapper.getMerchant(merchantId);
        if(merchatVO == null) {
            throw new RuntimeException("존재하지 않는 가맹점입니다.");
        }

        int result = merchantMapper.increaseSales(merchantId, amount);
        if(result == 0) {
            throw new RuntimeException("결제 오류");
        }
    }

    // 전체 카테고리 목록 조회
    @Override
    public List<MerchantCategoryDTO> getAllMerchantCategories() {
        List<MerchantCategoryVO> categoryVOS = merchantMapper.getAllMerchantCategories();
        return categoryVOS.stream()
                .map(MerchantCategoryDTO::of)
                .collect(Collectors.toList());
    }
}
