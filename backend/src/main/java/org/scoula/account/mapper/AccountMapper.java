package org.scoula.account.mapper;

import org.apache.ibatis.annotations.Param;

public interface AccountMapper {

    // 사용자 계좌 잔액 차감
    int decreaseUserBalance(@Param("userId") int userID, @Param("amount") int amount);

    // 가맹점 사업자 계좌 잔액 증가
    int increaseMerchantBalance(@Param("merchantId") int merchantID, @Param("amount") int amount);
}
