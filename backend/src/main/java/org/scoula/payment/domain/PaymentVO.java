package org.scoula.payment.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaymentVO {
    private int paymentId;
    private int tripId;
    private Integer merchantId;
    private int userId;
    private LocalDateTime payAt;
    private int amount;
    private String memo;
    private PaymentType paymentType;
}
