package org.scoula.payment.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ParticipantVO {
    private int participantId;
    private int paymentId;
    private int userId;
    private int splitAmount;
}
