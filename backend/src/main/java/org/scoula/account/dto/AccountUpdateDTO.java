package org.scoula.account.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AccountUpdateDTO {
    private int accountId;
    private int userId;
    private String accountNumber;
    private String bankName; //프론트에서 받는 은행명
    private String bankCode; //서버 내부 변환 후 저장
}
