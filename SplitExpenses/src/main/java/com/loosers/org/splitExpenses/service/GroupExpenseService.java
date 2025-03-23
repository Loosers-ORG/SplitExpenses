package com.loosers.org.splitExpenses.service;

import com.loosers.org.splitExpenses.ExpenseCalculator.ExpenseBalancer;
import com.loosers.org.splitExpenses.model.SettlementTransaction;
import com.loosers.org.splitExpenses.model.Expense;
import com.loosers.org.splitExpenses.model.GroupExpenseTable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class GroupExpenseService {

    GroupExpenseTable groupExpenseTable;

    @Autowired
    ExpenseBalancer expenseBalancer;

    GroupExpenseService() {
        groupExpenseTable = new GroupExpenseTable();
        groupExpenseTable.setUserExpenseMap(new HashMap<>());
    }

    public void addUser(String userId) {
        groupExpenseTable.getUserExpenseMap().put(userId, BigDecimal.ZERO);
    }

    public void addExpense(Expense expense) {
        BigDecimal amount = expense.getAmount();
        String paidBy = expense.getPaidBy();
        List<String> usersIncludedInExpense = expense.getUsersIncludedInExpense();
        Map<String, BigDecimal> groupExpenseTableUserExpenseMap = groupExpenseTable.getUserExpenseMap();

        groupExpenseTableUserExpenseMap.compute(paidBy, (k, existingAmount) ->
                (existingAmount == null ? BigDecimal.ZERO : existingAmount).add(amount)
        );
        BigDecimal amountPerUser = amount.divide(
                BigDecimal.valueOf(usersIncludedInExpense.size()),
                2,
                RoundingMode.HALF_UP
        );

        for (String user : usersIncludedInExpense) {
            groupExpenseTableUserExpenseMap.compute(user, (k, userExpense) ->
                    (userExpense == null ? BigDecimal.ZERO : userExpense).subtract(amountPerUser)
            );
        }
        System.out.println("Expense added to group expense table");

    }

    public List<SettlementTransaction> getSettlement(){
        return expenseBalancer.initializeQueue(groupExpenseTable.getUserExpenseMap());
    }


    public Map<String, BigDecimal> getGroupExpenseTableUserExpenseMap() {
        return groupExpenseTable.getUserExpenseMap();
    }
}
