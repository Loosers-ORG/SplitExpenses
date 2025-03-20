package com.loosers.org.splitExpenses.service;

import com.loosers.org.splitExpenses.model.Expense;
import com.loosers.org.splitExpenses.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@Service
public class ExpenseService {
    @Autowired UserService userService;

    HashMap<String, Expense> expenseMap = new HashMap<>();
    List<Expense> expenses;
    public void addExpense(Expense expense) {
        expenses.add(expense);
    }

    public List<Expense> getExpenses() {
        return expenses;
    }

    public HashMap<String, Expense> getExpenseMap(){
        return expenseMap;
    }

    public void addUserInExpense(String expenseId, String userId){
        Optional<Expense> foundExpense = Optional.ofNullable(expenseMap.get(expenseId));
        if (foundExpense.isEmpty()) {
            throw new RuntimeException("Expense with ID " + expenseId + " not found.");
        }
        Expense expense = foundExpense.get();
        User user = userService.getUserById(userId);


        expense.getUsersIncludedInExpense().add(user);
    }
}
