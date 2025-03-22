package com.loosers.org.splitExpenses.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@AllArgsConstructor
@Data
public class SettlementTransaction {
    private String groupId;
    private String sender;
    private String receiver;
    private BigDecimal amount;
    public SettlementTransaction(String sender, String receiver, BigDecimal amount){
        this.sender=sender;
        this.receiver=receiver;
        this.amount=amount;
    }
}
