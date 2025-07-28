package org.scoula.account.service;

import org.scoula.account.dto.AccountRegisterDTO;
import org.scoula.account.dto.AccountUpdateDTO;
import org.scoula.account.dto.AccountViewDTO;

public interface AccountService {

    // 계좌 등록
    void registerAccount(AccountRegisterDTO accountRegisterDTO);

    // 계좌 수정
    void updateAccount(AccountUpdateDTO accountUpdateDTO);

    // 계좌 조회
    AccountViewDTO getAccountByUserId(int userId);

    // 사용자 계좌 잔액 차감
    boolean decreaseUserBalance(int userId, int amount);

    // 사용자 계좌 잔액 확인
    int getBalanceByUserId(int userId);

    // 사용자 계좌 잔액 증가
    boolean increaseUserBalance(int userId, int amount);
}
