package org.scoula.account.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AccountRegisterDTO {
    private int userId;
    private String accountNumber;
    private String bankName;
}
