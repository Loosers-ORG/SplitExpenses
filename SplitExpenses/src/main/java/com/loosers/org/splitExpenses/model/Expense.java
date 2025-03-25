package com.loosers.org.splitExpenses.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Table(name = "Expenses")
public class Expense {
    @Id
    private String expenseId;

    @Column(nullable = false)
    private String name;

    private String description;

    @Column(name = "paid_by",nullable = false)
    private String paidBy;

    @Column(nullable = false)
    private BigDecimal amount;

    @ManyToOne
    @JoinColumn(name = "group_id") // Foreign key in Expense table
    private Group group;

    @ManyToMany
    @JoinTable(
            name = "expense_users",
            joinColumns = @JoinColumn(name = "expense_id"),
            inverseJoinColumns = @JoinColumn(name = "email")
    )
    private List<User> users;

    @Transient
    List<String> usersIncludedInExpense;
}
