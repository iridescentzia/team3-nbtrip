package org.scoula.paymentlist.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.scoula.paymentlist.domain.PaymentListVO;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentListDTO {
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

    /**
     * VO → DTO 변환
     */
    public static PaymentListDTO of(PaymentListVO vo) {
        if (vo == null) {
            return null;
        }
        return PaymentListDTO.builder()
                .paymentId(vo.getPaymentId())
                .merchantId(vo.getMerchantId())
                .merchantName(vo.getMerchantName())
                .categoryId(vo.getCategoryId())
                .categoryName(vo.getCategoryName())
                .userId(vo.getUserId())
                .nickname(vo.getNickname())
                .payAt(vo.getPayAt())
                .amount(vo.getAmount())
                .paymentType(vo.getPaymentType())
                .memo(vo.getMemo())
                .build();
    }

    /**
     * DTO → VO 변환
     */
    public PaymentListVO toVo() {
        return PaymentListVO.builder()
                .paymentId(this.paymentId)
                .merchantId(this.merchantId)
                .merchantName(this.merchantName)
                .categoryId(this.categoryId)
                .categoryName(this.categoryName)
                .userId(this.userId)
                .nickname(this.nickname)
                .payAt(this.payAt)
                .amount(this.amount)
                .paymentType(this.paymentType)
                .memo(this.memo)
                .build();
    }
}
