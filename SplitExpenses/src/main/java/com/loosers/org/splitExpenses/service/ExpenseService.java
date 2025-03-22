package com.loosers.org.splitExpenses.service;

import com.loosers.org.splitExpenses.model.Expense;
import com.loosers.org.splitExpenses.model.SettlementTransaction;
import com.loosers.org.splitExpenses.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@Service
public class ExpenseService {
    @Autowired
    UserService userService;

    @Autowired
    GroupService groupService;

    @Autowired
    GroupExpenseService groupExpenseService;

    HashMap<String, Expense> expenseMap = new HashMap<>();
    List<Expense> expenses;

    ExpenseService() {
        expenses = new ArrayList<>();
    }

    public void addExpense(Expense expense) {
        expenses.add(expense);
    }

    public String addExpenseToGroup(Expense expense, String groupId) {
        groupService.addExpenseToGroup(expense.getExpenseId(), groupId);
        groupExpenseService.addExpense(expense);
        return groupExpenseService.getSettlement().toString();
    }

    public void addUserInExpense(String expenseId, String userId) {
        Optional<Expense> foundExpense = Optional.ofNullable(expenseMap.get(expenseId));
        if (foundExpense.isEmpty()) {
            throw new RuntimeException("Expense with ID " + expenseId + " not found.");
        }
        Expense expense = foundExpense.get();
        expense.getUsersIncludedInExpense().add(userId);
    }

    public List<Expense> getExpenses() {
        return expenses;
    }

    public HashMap<String, Expense> getExpenseMap() {
        return expenseMap;
    }
}