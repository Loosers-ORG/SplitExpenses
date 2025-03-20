package com.loosers.org.splitExpenses.model;

import io.micrometer.common.lang.NonNull;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@AllArgsConstructor
@Data
public class Expense {
    private String expenseId;
    @NonNull
    private String name;
    private String description;
    @NonNull
    private String paidBy;

    List<User> usersIncludedInExpense;
}
