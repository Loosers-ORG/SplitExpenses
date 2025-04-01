package com.loosers.org.splitExpenses.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "GroupExpenseTable")
public class UserOutstandingBalances {
    @Id
    private String groupExpenseTableId;
    private String UserId;
    private BigDecimal outStandingAmount;

    @ManyToOne
    @JoinColumn(name = "group_id")
    private Group group;
}
