package org.scoula.account.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AccountVO {
    private int accountId;
    private int userId;
    private String accountNumber;
    private String bankCode;
    private int balance;
}
