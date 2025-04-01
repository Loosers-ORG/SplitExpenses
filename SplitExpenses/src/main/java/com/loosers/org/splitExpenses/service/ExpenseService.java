package com.loosers.org.splitExpenses.service;

import com.loosers.org.splitExpenses.model.Expense;
import com.loosers.org.splitExpenses.model.SettlementTransaction;
import com.loosers.org.splitExpenses.model.User;
import com.loosers.org.splitExpenses.repository.ExpenseRepository;
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

    @Autowired
    ExpenseRepository expenseRepository;


    public void addExpense(Expense expense) {
        expenseRepository.save(expense);
    }

    public List<SettlementTransaction> addExpenseToGroup(Expense expense, String groupId) {
        List<User> usersInExpense = userService.getUsersById(expense.getUsersIncludedInExpense());
        expense.setUsers(usersInExpense);
        groupService.addExpenseToGroup(expense, groupId);
        return groupExpenseService.addExpense(expense);
    }

    public Expense getExpenseById(String expenseId) {
        Optional<Expense> foundExpense = expenseRepository.findById(expenseId);
        if (foundExpense.isEmpty()) {
            throw new RuntimeException("Expense with ID " + expenseId + " not found.");
        }
        return foundExpense.get();
    }
}