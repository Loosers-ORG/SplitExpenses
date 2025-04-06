package com.loosers.org.splitExpenses.ExpenseCalculator;

import com.loosers.org.splitExpenses.model.SettlementTransaction;
import com.loosers.org.splitExpenses.model.UserOutstandingBalances;
import com.loosers.org.splitExpenses.service.SettlementTransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;

class UserTransaction {
    String userId;
    BigDecimal amount;
    public UserTransaction(String userId, BigDecimal amount) {
        this.userId = userId;
        this.amount = amount;
    }
}

@Service
public class ExpenseBalancer {
    @Autowired
    SettlementTransactionService settlementTransactionService;
    PriorityQueue<UserTransaction> receiversQueue = new PriorityQueue<>((a, b) -> b.amount.compareTo(a.amount));
    PriorityQueue<UserTransaction> sendersQueue = new PriorityQueue<>((a, b) -> b.amount.compareTo(a.amount));

    public List<SettlementTransaction> simplifyExpenses(String groupId, List<UserOutstandingBalances> userOutstandingBalances){
        settlementTransactionService.deleteSettlement(groupId);
        for(UserOutstandingBalances userOutstandingBalance: userOutstandingBalances){
            BigDecimal amount = userOutstandingBalance.getOutStandingAmount();
            if (amount.compareTo(BigDecimal.ZERO) < 0) {
                sendersQueue.add(new UserTransaction(userOutstandingBalance.getUserId(), amount.abs()));
            } else if (amount.compareTo(BigDecimal.ZERO) > 0) {
                receiversQueue.add(new UserTransaction(userOutstandingBalance.getUserId(), amount));
            }
        }
        balanceExpenses(groupId);
        return settlementTransactionService.getSettlementByGroup(groupId);
    }

    private void balanceExpenses(String groupId){
        while(!sendersQueue.isEmpty() && !receiversQueue.isEmpty()){
            UserTransaction senderTop = sendersQueue.poll();
            UserTransaction receiverTop = receiversQueue.poll();
            if (senderTop == null || receiverTop == null) {
                break;
            }
            BigDecimal paymentAmount = senderTop.amount.min(receiverTop.amount);
            settlementTransactionService.addSettlement(groupId,senderTop.userId,receiverTop.userId,paymentAmount);
            BigDecimal remainingAmount = receiverTop.amount.subtract(senderTop.amount);
            BigDecimal threshold = new BigDecimal("0.000000001");

            if(remainingAmount.abs().compareTo(BigDecimal.ONE) >= 0){
                if (remainingAmount.compareTo(threshold) > 0) {
                    receiversQueue.add(new UserTransaction(receiverTop.userId, remainingAmount));
                }
                else if (remainingAmount.compareTo(threshold.negate()) < 0) {
                    sendersQueue.add(new UserTransaction(senderTop.userId, remainingAmount.abs()));
                }
            }
        }
    }
}

