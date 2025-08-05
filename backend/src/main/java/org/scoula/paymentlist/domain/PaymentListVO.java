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

    /** 결제 내용 */
    private String memo;

    /** 결제자 */
    private String userId;

    private String nickname;

    /** "결제 날짜 */
    private String payAt;

    /** 결제금액 */
    private int amount;

}
