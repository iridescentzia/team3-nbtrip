package org.scoula.account.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.scoula.account.domain.AccountVO;
import org.scoula.account.domain.BankCode;
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

    // 공통 메서드 - 조회 + null 체크
    @Override
    public AccountVO getVerifiedAccountByUserId(int userId) {
        AccountVO accountVO = accountMapper.selectAccountByUserId(userId);
        if (accountVO == null) {
            throw new RuntimeException("존재하지 않는 계좌입니다.");
        }
        return accountVO;
    }

    // 계좌 등록
    @Override
    public void registerAccount(AccountRegisterDTO accountRegisterDTO) {
        try {
            String bankCode = BankCode.fromName(accountRegisterDTO.getBankName()).getCode();
            accountRegisterDTO.setBankCode(bankCode);
        } catch (IllegalArgumentException e) {
            log.warn("등록되지 않은 은행명 입력: {}", e.getMessage());
            throw new RuntimeException("지원하지 않는 은행입니다.");
        }
        accountMapper.insertAccount(accountRegisterDTO);
    }

    // 계좌 수정
    @Override
    public void updateAccount(AccountUpdateDTO accountUpdateDTO) {
        try {
            String bankCode = BankCode.fromName(accountUpdateDTO.getBankName()).getCode();
            accountUpdateDTO.setBankCode(bankCode);
        } catch (IllegalArgumentException e) {
            log.warn("등록되지 않은 은행명 입력: {}", e.getMessage());
            throw new RuntimeException("지원하지 않는 은행입니다.");
        }
        accountMapper.updateAccount(accountUpdateDTO);
    }

    // 계좌 조회 - 외부에 노출할 DTO 변환 메서드
    @Override
    public AccountViewDTO getAccountByUserId(int userId) {
        AccountVO accountVO = getVerifiedAccountByUserId(userId);  // 공통 메서드 사용
        return AccountViewDTO.builder()
                .accountId(accountVO.getAccountId())
                .userId(accountVO.getUserId())
                .accountNumber(accountVO.getAccountNumber())
                .bankCode(accountVO.getBankCode())
                .balance(accountVO.getBalance())
                .build();
    }

    // 사용자 계좌 잔액 차감
    @Override
    public void decreaseUserBalance(int userId, int amount) {
        AccountVO accountVO = getVerifiedAccountByUserId(userId);  // 공통 메서드 사용

        if (accountVO.getBalance() < amount) {
            throw new RuntimeException("잔액 부족");
        }

        int result = accountMapper.decreaseUserBalance(userId, amount);
        if (result == 0) {
            throw new RuntimeException("잔액 차감 실패");
        }
    }

    // 사용자 계좌 잔액 확인
    @Override
    public int getBalanceByUserId(int userId) {
        AccountVO accountVO = getVerifiedAccountByUserId(userId);  // 공통 메서드 사용
        return accountVO.getBalance();
    }

    // 사용자 계좌 잔액 증가
    @Override
    public int increaseUserBalance(int userId, int amount) {
        return accountMapper.increaseUserBalance(userId, amount);
    }
}
