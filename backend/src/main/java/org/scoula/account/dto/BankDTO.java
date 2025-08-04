package org.scoula.account.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class BankDTO {
    private String bankCode;
    private String bankName;
}
