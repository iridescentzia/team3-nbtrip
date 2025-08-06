package org.scoula.payment.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.scoula.account.service.AccountService;
import org.scoula.member.domain.MemberVO;
import org.scoula.member.mapper.MemberMapper;
import org.scoula.merchant.domain.MerchantVO;
import org.scoula.merchant.mapper.MerchantMapper;
import org.scoula.merchant.service.MerchantService;
import org.scoula.notification.dto.NotificationDTO;
import org.scoula.notification.service.NotificationService;
import org.scoula.payment.domain.ParticipantVO;
import org.scoula.payment.domain.PaymentType;
import org.scoula.payment.domain.PaymentVO;
import org.scoula.payment.dto.ParticipantDTO;
import org.scoula.payment.dto.PaymentDTO;
import org.scoula.payment.mapper.ParticipantMapper;
import org.scoula.payment.mapper.PaymentMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Log4j2
public class PaymentServiceImpl implements PaymentService {

    private final PaymentMapper paymentMapper;
    private final ParticipantMapper participantMapper;
    private final AccountService accountService;
    private final MerchantService merchantService;
    private final MerchantMapper merchantMapper;
    private final MemberMapper memberMapper;
    private final NotificationService notificationService;

    /* QR 결제 처리 */
    @Override
    @Transactional
    public void processPayment(PaymentDTO paymentDTO, int userId, int tripId) {
        validatePayment(paymentDTO, userId);

        // 결제 전 잔액 확인
        int beforeBalance = accountService.getBalanceByUserId(userId);

        // 사용자 계좌 잔액 차감
        accountService.decreaseUserBalance(userId, paymentDTO.getAmount());

        // 결제 후 잔액 확인(검증)
        int afterBalance = accountService.getBalanceByUserId(userId);
        if(afterBalance != beforeBalance - paymentDTO.getAmount()) {
            throw new RuntimeException("결제 금액 오류");
        }

        // 사업자 매출 반영
        merchantService.increaseSales(paymentDTO.getMerchantId(), paymentDTO.getAmount());

        // 결제 내역 저장
        PaymentVO paymentVO = savePaymentRecord(paymentDTO, PaymentType.QR, LocalDateTime.now(), paymentDTO.getMerchantId(), userId, tripId);

        // 결제 참여자 저장 및 금액 분배
        saveParticipants(paymentDTO, paymentVO, true, userId);

        // 결제 알림 생성
        MemberVO member = memberMapper.findById(userId);
        String fromUserNickname = member != null ? member.getNickname() : "알 수 없음";

        MerchantVO merchant = merchantMapper.getMerchant(paymentDTO.getMerchantId());
        String merchantName = merchant != null ? merchant.getMerchantName() : "알 수 없음";

        NotificationDTO notificationDTO = NotificationDTO.builder()
                .userId(userId)
                .fromUserId(userId)
                .tripId(tripId)
                .paymentId(paymentVO.getPaymentId())
                .notificationType("TRANSACTION")
                .actionType("CREATE")
                .fromUserNickname(fromUserNickname)
                .merchantName(merchantName)
                .build();
        notificationService.createNotification(notificationDTO);
    }


    /* 선결제/기타 결제 수동 등록 */
    @Override
    @Transactional
    public void registerManualPayment(PaymentDTO paymentDTO, int userId, int tripId) {
        validatePayment(paymentDTO, userId);

        // 수동 결제 시 사용자가 날짜/시간 입력하면 병합, 아니면 현재 시간
        LocalDateTime payAt = (paymentDTO.getPaymentDate() != null && paymentDTO.getPaymentTime() != null)
                ? LocalDateTime.of(paymentDTO.getPaymentDate(), paymentDTO.getPaymentTime())
                : LocalDateTime.now();

        // 카테고리(merchantId)
        int merchantId = paymentDTO.getMerchantId();

        // 결제 내역 저장
        PaymentVO paymentVO = savePaymentRecord(paymentDTO, paymentDTO.getPaymentType(), payAt, merchantId, userId, tripId);

        // 결제 참여자 저장 및 금액 분배
        saveParticipants(paymentDTO, paymentVO, false, userId);

        // 결제 알림 생성
        NotificationDTO notificationDTO = NotificationDTO.builder()
                .userId(userId)
                .fromUserId(userId)
                .tripId(tripId)
                .paymentId(paymentVO.getPaymentId())
                .notificationType("TRANSACTION")
                .actionType("CREATE")
                .build();
        notificationService.createNotification(notificationDTO);
    }

    /* 검증 */
    private void validatePayment(PaymentDTO paymentDTO, int userId) {
        if (paymentDTO.getAmount() <= 0) {
            throw new IllegalArgumentException("결제 금액은 0원보다 커야 합니다.");
        }
        List<ParticipantDTO> participants = paymentDTO.getParticipants();
        if (participants == null || participants.isEmpty()) {
            throw new IllegalArgumentException("참여자는 최소 1명 이상이어야 합니다.");
        }
        boolean payerIncluded = participants.stream()
                .anyMatch(p -> p.getUserId() == userId);
        if (!payerIncluded) {
            throw new IllegalArgumentException("결제자는 반드시 결제 참여자에 포함되어야 합니다.");
        }
    }


    /* 결제 내역 저장 */
    private PaymentVO savePaymentRecord(PaymentDTO paymentDTO, PaymentType paymentType, LocalDateTime payAt, int merchantId, int userId, int tripId) {
        PaymentVO paymentVO = new PaymentVO();
        paymentVO.setTripId(tripId);
        paymentVO.setMerchantId(merchantId);
        paymentVO.setUserId(userId);
        paymentVO.setAmount(paymentDTO.getAmount());
        paymentVO.setPaymentType(paymentType);
        paymentVO.setPayAt(payAt);
        paymentVO.setMemo(paymentDTO.getMemo());

        int inserted = paymentMapper.insertPayment(paymentVO);
        if (inserted == 0) {
            throw new RuntimeException("결제 내역 저장 실패");
        }
        return paymentVO;
    }


    /* 결제 참여자 저장 및 금액 분배 */
    private void saveParticipants(PaymentDTO paymentDTO, PaymentVO paymentVO, boolean isAutoSplit, int userId) {
        List<ParticipantDTO> participants = paymentDTO.getParticipants();
        int participantCount = participants.size();
        int baseSplitAmount = paymentDTO.getAmount() / participantCount;
        int remainderAmount = paymentDTO.getAmount() % participantCount;

        boolean allSplitAmountZero = participants.stream()
                .allMatch(p -> p.getSplitAmount() == 0);

        List<ParticipantVO> participantVOList = participants.stream()
                .map(p -> {
                    int splitAmount;
                    if (isAutoSplit || allSplitAmountZero) {
                        splitAmount = baseSplitAmount;
                        if (Integer.valueOf(p.getUserId()).equals(userId)) {
                            splitAmount += remainderAmount;
                        }
                    } else {
                        splitAmount = p.getSplitAmount();
                    }
                    return ParticipantVO.builder()
                            .paymentId(paymentVO.getPaymentId())
                            .userId(p.getUserId())
                            .splitAmount(splitAmount)
                            .build();
                })
                .toList();

        int totalSplit = participantVOList.stream().mapToInt(ParticipantVO::getSplitAmount).sum();
        if (totalSplit != paymentDTO.getAmount()) {
            throw new RuntimeException("분배 금액 총합 오류");
        }

        int result = participantMapper.insertParticipants(participantVOList);
        if (result != participantCount) {
            log.error("참여자 저장 실패");
            throw new RuntimeException("결제 참여자 저장 실패");
        }
    }

    /* QR 결제 상세 수정 */
    @Override
    @Transactional
    public void updateQrPayment(int paymentId, PaymentDTO paymentDTO, int userId) {
        validatePayment(paymentDTO, userId);

        // 기존 결제 조회
        PaymentVO existingPayment = paymentMapper.selectPaymentById(paymentId);
        if (existingPayment == null) {
            throw new RuntimeException("결제 내역이 존재하지 않습니다.");
        }

        // 메모 수정
        paymentMapper.updateMemo(paymentId, paymentDTO.getMemo());

        // 결제 참여자 및 금액 분배 수정
        syncParticipants(paymentDTO, existingPayment, false, userId);

        // 수정 알림 생성
        NotificationDTO notificationDTO = NotificationDTO.builder()
                .userId(userId)
                .fromUserId(userId)
                .tripId(existingPayment.getTripId())
                .paymentId(existingPayment.getPaymentId())
                .notificationType("TRANSACTION")
                .actionType("UPDATE")
                .build();
        notificationService.createNotification(notificationDTO);
    }


    /* 선결제/기타 결제 상세 수정 */
    @Override
    @Transactional
    public void updateManualPayment(int paymentId, PaymentDTO paymentDTO, int userId) {
        validatePayment(paymentDTO, userId);

        LocalDateTime payAt = (paymentDTO.getPaymentDate() != null && paymentDTO.getPaymentTime() != null)
                ? LocalDateTime.of(paymentDTO.getPaymentDate(), paymentDTO.getPaymentTime())
                : LocalDateTime.now();

        PaymentVO existingPayment = paymentMapper.selectPaymentById(paymentId);
        if (existingPayment == null) {
            throw new RuntimeException("결제 내역이 존재하지 않습니다.");
        }

        PaymentVO updatedPayment = new PaymentVO();
        updatedPayment.setPaymentId(paymentId);
        updatedPayment.setUserId(userId);
        updatedPayment.setTripId(existingPayment.getTripId()); // 기존값 유지
        updatedPayment.setAmount(paymentDTO.getAmount());
        updatedPayment.setMerchantId(paymentDTO.getMerchantId());
        updatedPayment.setMemo(paymentDTO.getMemo());
        updatedPayment.setPayAt(payAt);

        int result = paymentMapper.updateManualPayment(updatedPayment);
        if (result == 0) throw new RuntimeException("결제 수정 실패");

        // 결제 참여자 및 금액 분배 수정
        syncParticipants(paymentDTO, updatedPayment, false, userId);

        // 수정 알림 생성
        NotificationDTO notificationDTO = NotificationDTO.builder()
                .userId(userId)
                .fromUserId(userId)
                .tripId(existingPayment.getTripId())
                .paymentId(existingPayment.getPaymentId())
                .notificationType("TRANSACTION")
                .actionType("UPDATE")
                .build();
        notificationService.createNotification(notificationDTO);
    }


    /* 결제 참여자 및 분배 금액 수정 */
    private void syncParticipants(PaymentDTO paymentDTO, PaymentVO paymentVO, boolean isAutoSplit, int payerId) {
        int paymentId = paymentVO.getPaymentId();

        // 기존 참여자 조회
        List<ParticipantVO> existingParticipants = participantMapper.findByPaymentId(paymentId);
        Map<Integer, ParticipantVO> existingMap = existingParticipants.stream()
                .collect(Collectors.toMap(ParticipantVO::getUserId, vo -> vo));

        // 새 참여자 목록
        List<ParticipantDTO> newParticipants = paymentDTO.getParticipants();
        Map<Integer, ParticipantDTO> newMap = newParticipants.stream()
                .collect(Collectors.toMap(ParticipantDTO::getUserId, dto -> dto));

        // 삭제 대상 처리
        for (Integer existingUserId : existingMap.keySet()) {
            if (!newMap.containsKey(existingUserId)) {
                int deletedRows = participantMapper.deleteParticipant(paymentId, existingUserId);
                if (deletedRows != 1) {
                    throw new RuntimeException("결제 참여자 삭제 실패 - userId: " + existingUserId);
                }
            }
        }

        // 추가 대상 처리
        for (ParticipantDTO participantDTO : newParticipants) {
            if (!existingMap.containsKey(participantDTO.getUserId())) {
                ParticipantVO newParticipantVO = ParticipantVO.builder()
                        .paymentId(paymentId)
                        .userId(participantDTO.getUserId())
                        .splitAmount(0) // 분배 전 초기값
                        .build();
                int insertedRows = participantMapper.insertParticipant(newParticipantVO);
                if (insertedRows != 1) {
                    throw new RuntimeException("결제 참여자 추가 실패 - userId: " + participantDTO.getUserId());
                }
            }
        }

        // 금액 분배 계산
        int participantCount = newParticipants.size();
        int baseSplitAmount = paymentDTO.getAmount() / participantCount;
        int remainderAmount = paymentDTO.getAmount() % participantCount;

        boolean allSplitAmountZero = newParticipants.stream()
                .allMatch(p -> p.getSplitAmount() == 0);

        List<ParticipantVO> updatedParticipants = newParticipants.stream()
                .map(p -> {
                    int splitAmount;
                    if (isAutoSplit || allSplitAmountZero) {
                        splitAmount = baseSplitAmount;
                        if (Integer.valueOf(p.getUserId()).equals(payerId)) {
                            splitAmount += remainderAmount;
                        }
                    } else {
                        splitAmount = p.getSplitAmount();
                    }
                    return ParticipantVO.builder()
                            .paymentId(paymentId)
                            .userId(p.getUserId())
                            .splitAmount(splitAmount)
                            .build();
                })
                .toList();

        // 검증
        int totalSplit = updatedParticipants.stream().mapToInt(ParticipantVO::getSplitAmount).sum();
        if (totalSplit != paymentDTO.getAmount()) {
            throw new RuntimeException("분배 금액 총합 오류");
        }

        // 분배 금액 update
        for (ParticipantVO updated : updatedParticipants) {
            ParticipantVO existing = existingMap.get(updated.getUserId());
            if (existing == null || existing.getSplitAmount() != updated.getSplitAmount()) {
                int updatedRows = participantMapper.updateAmount(paymentId, updated.getUserId(), updated.getSplitAmount());
                if (updatedRows != 1) {
                    throw new RuntimeException("분배 금액 업데이트 실패 - userId: " + updated.getUserId());
                }
            }
        }
    }
}
