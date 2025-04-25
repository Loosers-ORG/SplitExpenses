package com.loosers.org.splitExpenses.service;

import com.loosers.org.splitExpenses.model.SettlementTransaction;
import com.loosers.org.splitExpenses.repository.SettlementTransactionRepo;
import com.loosers.org.splitExpenses.utils.IdGenerator;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class SettlementTransactionService {

    @Autowired
    SettlementTransactionRepo settlementTransactionRepo;

    @Autowired
    IdGenerator idGenerator;

    public List<SettlementTransaction> getSettlementByGroup(String groupId) {
        return settlementTransactionRepo.findAllByGroupId(groupId);
    }

    public void addSettlement(String groupId, String senderId, String receiverId, BigDecimal amount){
        String settlementId = idGenerator.generteId(groupId, senderId, receiverId);
        settlementTransactionRepo.save(new SettlementTransaction(settlementId,groupId,senderId,receiverId,amount));
    }

    @Transactional
    public void deleteSettlement(String groupId) {
        settlementTransactionRepo.deleteByGroupId(groupId);
    }

    public void saveAll(List<SettlementTransaction> settlements) {
        settlementTransactionRepo.saveAll(settlements);
    }
}
