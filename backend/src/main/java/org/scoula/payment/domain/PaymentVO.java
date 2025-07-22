package org.scoula.payment.domain;

import java.time.LocalDateTime;

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
