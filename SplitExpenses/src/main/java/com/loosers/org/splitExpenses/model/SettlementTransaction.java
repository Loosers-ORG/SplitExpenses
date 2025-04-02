package com.loosers.org.splitExpenses.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Table(name = "SettlementTransactions")
public class SettlementTransaction {
    @Id
    private String settlementId;
    private String groupId;
    private String sender;
    private String receiver;
    private BigDecimal amount;
}
