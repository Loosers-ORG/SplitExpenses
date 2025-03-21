package com.loosers.org.splitExpenses.service;

import com.loosers.org.splitExpenses.model.Expense;
import com.loosers.org.splitExpenses.model.GroupExpenseTable;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class GroupExpenseService {

    GroupExpenseTable groupExpenseTable;

    GroupExpenseService() {
        groupExpenseTable = new GroupExpenseTable();
        groupExpenseTable.setUserExpenseMap(new HashMap<>());
    }

    public void addUser(String userId) {
        groupExpenseTable.getUserExpenseMap().put(userId, 0.0);
    }

    public void addExpense(Expense expense) {
        Double amount = expense.getAmount();
        String paidBy = expense.getPaidBy();
        List<String> usersIncludedInExpense = expense.getUsersIncludedInExpense();
        Map<String, Double> groupExpenseTableUserExpenseMap = groupExpenseTable.getUserExpenseMap();

        groupExpenseTableUserExpenseMap.compute(paidBy, (k, existingAmount) -> existingAmount + amount);
        double amountPerUser = amount / usersIncludedInExpense.size();

        for (String user : usersIncludedInExpense) {
            groupExpenseTableUserExpenseMap.compute(user, (k, userExpense) -> userExpense - amountPerUser);
        }
        System.out.println("Expense added to group expense table");

    }

    public Map<String, Double> getGroupExpenseTableUserExpenseMap() {
        return groupExpenseTable.getUserExpenseMap();
    }
}
