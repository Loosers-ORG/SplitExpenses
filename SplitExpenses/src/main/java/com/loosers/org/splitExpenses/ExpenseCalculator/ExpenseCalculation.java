package com.loosers.org.splitExpenses.ExpenseCalculator;


import com.loosers.org.splitExpenses.service.ExpenseService;
import org.springframework.beans.factory.annotation.Autowired;

public class ExpenseCalculation {

    @Autowired
    private ExpenseService expenseService;

    // triggered when a new expense is added
    public void addToExpenseTable(String expenseId, String groupId) {

    }
}
