package org.scoula.paymentlist.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 결제 리스트에 사용할 도메인 VO.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentListVO {
    private int paymentId;
    private int merchantId;
    private String merchantName;

    private int categoryId;
    private String categoryName;

    private int userId;
    private String nickname;

    private String payAt;
    private int amount;
    private String paymentType;
    private String memo;

}
