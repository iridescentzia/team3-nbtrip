package org.scoula.account.mapper;

import org.apache.ibatis.annotations.Param;
import org.scoula.account.domain.AccountVO;
import org.scoula.account.dto.AccountRegisterDTO;
import org.scoula.account.dto.AccountUpdateDTO;

public interface AccountMapper {

    // 계좌 등록 - 회원가입 시 사용
    int insertAccount(AccountRegisterDTO accountRegisterDTO);

    // 계좌 수정 - 마이페이지 결제 수단 관리
    int updateAccount(AccountUpdateDTO accountUpdateDTO);

    // 계좌 상세 조회 - 홈 화면 계좌 잔액 표시용
    AccountVO selectAccountByUserId(@Param("userId") int userId);

    // 사용자 계좌 잔액 차감 - 송금/결제 시 사용
    int decreaseUserBalance(@Param("userId") int userId, @Param("amount") int amount);

    // 사용자 계좌 잔액 증가 - 정산 송금 시 사용
    int increaseUserBalance(@Param("userId") int userId, @Param("amount") int amount);

    // 가맹점 사업자 계좌 잔액 증가 - QR 결제 시 사용
    int increaseMerchantBalance(@Param("merchantId") int merchantId, @Param("amount") int amount);
}
