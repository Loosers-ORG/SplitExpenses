package com.loosers.org.splitExpenses.service;

import com.loosers.org.splitExpenses.ExpenseCalculator.ExpenseBalancer;
import com.loosers.org.splitExpenses.model.SettlementTransaction;
import com.loosers.org.splitExpenses.model.UserOutstandingBalances;
import com.loosers.org.splitExpenses.repository.SettlementTransactionRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class SettlementTransactionService {

    @Autowired
    SettlementTransactionRepo settlementTransactionRepo;

    public List<SettlementTransaction> getSettlementByGroup(String groupId) {
        return settlementTransactionRepo.findAllByGroupId(groupId);
    }

    public void addSettlement(String groupId, String senderId, String receiverId, BigDecimal amount){
        String settlementId = generateCompositeId(groupId, senderId, receiverId);
        settlementTransactionRepo.save(new SettlementTransaction(settlementId,groupId,senderId,receiverId,amount));
    }

    private String generateCompositeId(String groupId, String senderId, String receiverId){
        return groupId.concat(senderId).concat(receiverId);
    }
}
