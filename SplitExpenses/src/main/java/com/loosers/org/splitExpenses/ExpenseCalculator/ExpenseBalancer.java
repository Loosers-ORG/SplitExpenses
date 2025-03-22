package com.loosers.org.splitExpenses.ExpenseCalculator;

import com.loosers.org.splitExpenses.model.SettlementTransaction;
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
    public List<SettlementTransaction> settlementTransaction;

    PriorityQueue<UserTransaction> receiversQueue = new PriorityQueue<>((a, b) -> b.amount.compareTo(a.amount));
    PriorityQueue<UserTransaction> sendersQueue = new PriorityQueue<>((a, b) -> b.amount.compareTo(a.amount));

    public List<SettlementTransaction> initializeQueue(Map<String, BigDecimal> transactions){
        settlementTransaction = new ArrayList<>();
        transactions.forEach((userId, amount)-> {
            if (amount.compareTo(BigDecimal.ZERO) < 0) {
                sendersQueue.add(new UserTransaction(userId, amount.abs()));
            } else if (amount.compareTo(BigDecimal.ZERO) > 0) {
                receiversQueue.add(new UserTransaction(userId, amount));
            }
        });
        balanceExpenses();
        return settlementTransaction;
    }

    private void balanceExpenses(){
        while(!sendersQueue.isEmpty() && !receiversQueue.isEmpty()){
            UserTransaction senderTop = sendersQueue.poll();
            UserTransaction receiverTop = receiversQueue.poll();
            if (senderTop == null || receiverTop == null) {
                break;
            }

            BigDecimal paymentAmount = senderTop.amount.min(receiverTop.amount);
            settlementTransaction.add(new SettlementTransaction(senderTop.userId,receiverTop.userId,paymentAmount));
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

