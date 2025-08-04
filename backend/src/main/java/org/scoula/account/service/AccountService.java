package org.scoula.account.service;

import org.scoula.account.domain.AccountVO;
import org.scoula.account.dto.AccountRegisterDTO;
import org.scoula.account.dto.AccountUpdateDTO;
import org.scoula.account.dto.AccountViewDTO;
import org.scoula.account.dto.BankDTO;

import java.util.List;

public interface AccountService {

    // 은행 목록 조회
    List<BankDTO> getBankList();

    // 계좌 조회
    AccountViewDTO getAccountByUserId(int userId);

    // 계좌 등록
    void registerAccount(AccountRegisterDTO accountRegisterDTO);

    // 계좌 수정
    void updateAccount(AccountUpdateDTO accountUpdateDTO);

    // 사용자 계좌 잔액 차감
    void decreaseUserBalance(int userId, int amount);

    // 사용자 계좌 잔액 확인
    int getBalanceByUserId(int userId);

    // 게좌 조회 및 존재 여부 확인(검증)
    AccountVO getVerifiedAccountByUserId(int userId);
}
