package org.scoula.settlement.mapper;

import org.apache.ibatis.annotations.Param;

public interface SettlementAccountMapper {

    Integer selectBalance(@Param("userId") int userId);

    // 송금 : 잔액 조건부 차감
    // 성공 1, 실패(잔액 부족/경합) 0
    int debitIfEnough(
            @Param("userId") int userId,
            @Param("amount") int amount
    );

    // 입금 : 단순 증가
    int credit(
            @Param("userId") int userId,
            @Param("amount") int amount
    );
}
