package org.scoula.account.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.scoula.account.domain.AccountVO;
import org.scoula.account.dto.AccountRegisterDTO;
import org.scoula.account.dto.AccountUpdateDTO;
import org.scoula.account.dto.AccountViewDTO;
import org.scoula.account.mapper.AccountMapper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Log4j2
public class AccountServiceImpl implements AccountService {

    private final AccountMapper accountMapper;

    // 계좌 등록
    @Override
    public void registerAccount(AccountRegisterDTO accountRegisterDTO) {
        accountMapper.insertAccount(accountRegisterDTO);
    }

    // 계좌 수정
    @Override
    public void updateAccount(AccountUpdateDTO accountUpdateDTO) {
        accountMapper.updateAccount(accountUpdateDTO);
    }

    // 계좌 조회
    @Override
    public AccountViewDTO getAccountByUserId(int userId) {
        AccountVO accountVO = accountMapper.selectAccountByUserId(userId);
        return AccountViewDTO.builder()
                .accountId(accountVO.getAccountId())
                .userId(accountVO.getUserId())
                .accountNumber(accountVO.getAccountNumber())
                .bankName(accountVO.getBankName())
                .balance(accountVO.getBalance())
                .build();
    }

    // 사용자 계좌 잔액 차감
    @Override
    public boolean decreaseUserBalance(int userId, int amount) {
        AccountVO accountVO = accountMapper.selectAccountByUserId(userId);
        if(accountVO == null) {
            log.warn("존재하지 않는 사용자 ID입니다.: {}", userId);
            return false;
        }

        if(accountVO.getBalance() < amount) {
            log.warn("잔액 부족: userId={}, 현재 잔액={}, 요청 금액={}", userId, accountVO.getBalance(), amount);
            return false;
        }

        return accountMapper.decreaseUserBalance(userId, amount) > 0;
    }

    // 사용자 게좌 잔액 증가
    @Override
    public boolean increaseUserBalance(int userId, int amount) {
        return accountMapper.increaseUserBalance(userId, amount) > 0;
    }

    //사업자 계좌 잔액 증가
    @Override
    public boolean increaseMerchantBalance(int merchantId, int amount) {
        int updated = accountMapper.increaseMerchantBalance(merchantId, amount);
        if(updated == 0) {
            log.warn("존재하지 않는 가맹점 ID입니다. {}", merchantId);
            throw new RuntimeException("가맹점 계좌가 존재하지 않습니다.");
        }
        return true;
    }
}
