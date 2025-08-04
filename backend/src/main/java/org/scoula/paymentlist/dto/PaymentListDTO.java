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

    private String memo;

    private String userId;

    private String nickname;

    private String payAt;

    private int amount;
    /**
     * VO → DTO 변환
     */
    public static PaymentListDTO of(PaymentListVO vo) {
        if (vo == null) {
            return null;
        }
        return PaymentListDTO.builder()
                .memo(vo.getMemo())
                .userId(vo.getUserId())
                .nickname(vo.getNickname())
                .payAt(vo.getPayAt())
                .amount(vo.getAmount())
                .build();
    }

    /**
     * DTO → VO 변환
     */
    public PaymentListVO toVo() {
        return PaymentListVO.builder()
                .memo(this.memo)
                .userId(this.userId)
                .nickname(this.nickname)
                .payAt(this.payAt)
                .amount(this.amount)
                .build();
    }
}
