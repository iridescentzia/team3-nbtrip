package org.scoula.settlement.service;

import lombok.extern.log4j.Log4j2;
import org.scoula.settlement.dto.SettlementDTO.RawSettlementDataDTO;
import org.scoula.settlement.dto.SettlementDTO;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * '상계 기반 직접 정산 알고리즘'을 사용하여 정산 계산 로직을 처리하는 클래스.
 */
@Component
@Log4j2
public class SettlementCalculator {

    public List<SettlementDTO.OptimizedTransaction> calculate(List<RawSettlementDataDTO> rawData, List<Integer> memberIds) {

        // 1. 채무 매트릭스 생성
        Map<Integer, Map<Integer, Double>> debts = new HashMap<>();
        for (Integer memberId : memberIds) {
            debts.put(memberId, new HashMap<>());
        }
        log.info("1. 채무 매트릭스 초기화 완료. 멤버: {}", memberIds);

        // 2. 각 결제 건별로 채무 관계 분석 및 기록
        for (RawSettlementDataDTO data : rawData) {
            Integer payerId = data.getPayerId();
            Integer participantId = data.getParticipantId();
            double splitAmount = data.getSplitAmount();

            if (!payerId.equals(participantId)) {
                Map<Integer, Double> participantDebts = debts.get(participantId);
                participantDebts.put(payerId, participantDebts.getOrDefault(payerId, 0.0) + splitAmount);
            }
        }
        log.info("2. 상계 전 채무 기록 완료 (총 채무 매트릭스):");
        debts.forEach((sender, receiverMap) -> {
            if (!receiverMap.isEmpty()) {
                log.info("   - {}가 갚아야 할 돈: {}", sender, receiverMap);
            }
        });

        // 3. 두 사람 간의 채무를 '상계(Netting)' 처리
        List<Integer> memberList = new ArrayList<>(memberIds);
        for (int i = 0; i < memberList.size(); i++) {
            for (int j = i + 1; j < memberList.size(); j++) {
                Integer personA = memberList.get(i);
                Integer personB = memberList.get(j);

                double aToBDebt = debts.get(personA).getOrDefault(personB, 0.0);
                double bToADebt = debts.get(personB).getOrDefault(personA, 0.0);

                if (aToBDebt > bToADebt) {
                    debts.get(personA).put(personB, aToBDebt - bToADebt);
                    debts.get(personB).put(personA, 0.0);
                } else {
                    debts.get(personB).put(personA, bToADebt - aToBDebt);
                    debts.get(personA).put(personB, 0.0);
                }
            }
        }
        log.info("3. 상계 처리 후 채무 기록 완료:");
        debts.forEach((sender, receiverMap) -> {
            if (!receiverMap.isEmpty() && receiverMap.values().stream().anyMatch(amount -> amount > 0)) {
                log.info("   - {}가 갚아야 할 돈: {}", sender, receiverMap);
            }
        });

        // 4. 최종 송금 목록 생성
        List<SettlementDTO.OptimizedTransaction> finalTransactions = new ArrayList<>();
        debts.forEach((senderId, receivers) -> {
            receivers.forEach((receiverId, amount) -> {
                if (amount > 0) {
                    SettlementDTO.OptimizedTransaction tx = new SettlementDTO.OptimizedTransaction();
                    tx.setSenderId(senderId);
                    tx.setReceiverId(receiverId);
                    tx.setAmount((int) Math.round(amount));
                    finalTransactions.add(tx);
                }
            });
        });
        log.info("4. 최종 송금 목록 생성 완료.");

        return finalTransactions;
    }
}