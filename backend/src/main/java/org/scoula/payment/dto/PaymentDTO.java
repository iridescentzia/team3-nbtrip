package org.scoula.payment.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.scoula.payment.domain.PaymentType;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PaymentDTO {
    private int tripId;
    private int merchantId;
    private int amount;
    private String memo;
    private PaymentType paymentType;
    private int payerId;

    // 결제 참여자
    private List<ParticipantDTO> participants;

    // 사용자가 입력한 결제일자 및 시간
//    private LocalDate paymentDate;
//    private LocalTime paymentTime;
    private LocalDateTime payAt;

}
