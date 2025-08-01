package org.scoula.payment.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.scoula.payment.domain.PaymentType;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PaymentDTO {
    private int tripId;
    private int userId;
    private int merchantId;
    private int amount;
    private PaymentType paymentType;

    // 결제 참여자
    private List<ParticipantDTO> participants;
}
