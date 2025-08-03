package org.scoula.account.service;

import org.scoula.account.domain.AccountVO;
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
    void decreaseUserBalance(int userId, int amount);

    // 사용자 계좌 잔액 확인
    int getBalanceByUserId(int userId);

    // 사용자 계좌 잔액 증가
    int increaseUserBalance(int userId, int amount);

    // 게좌 조회 및 존재 여부 확인(검증)
    AccountVO getVerifiedAccountByUserId(int userId);
}
