package com.loosers.org.splitExpenses.service;

import com.loosers.org.splitExpenses.ExpenseCalculator.ExpenseBalancer;
import com.loosers.org.splitExpenses.model.Group;
import com.loosers.org.splitExpenses.model.SettlementTransaction;
import com.loosers.org.splitExpenses.model.Expense;
import com.loosers.org.splitExpenses.model.UserOutstandingBalances;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class GroupExpenseService {

    UserOutstandingBalances userOutstandingBalances;

    @Autowired
    ExpenseBalancer expenseBalancer;

    @Autowired
    GroupService groupService;

    @Autowired
    GroupExpenseTableService groupExpenseTableService;

    public List<SettlementTransaction>  addExpense(Expense expense) {
        BigDecimal amount = expense.getAmount();
        String paidBy = expense.getPaidBy();
        List<String> usersIncludedInExpense = expense.getUsersIncludedInExpense();
        Group group = expense.getGroup();
        List<UserOutstandingBalances> userOutstandingBalances = group.getUserOutstandingBalances();

        BigDecimal amountPerUser = amount.divide(
                BigDecimal.valueOf(usersIncludedInExpense.size()),
                2,
                RoundingMode.HALF_UP
        );

        for(UserOutstandingBalances userOutstandingBalance : userOutstandingBalances) {
            String userId = userOutstandingBalance.getUserId();
            if (userId.equals(paidBy)) {
                userOutstandingBalance.setOutStandingAmount(userOutstandingBalance.getOutStandingAmount().add(amount.subtract(amountPerUser)));
            } else if (usersIncludedInExpense.contains(userId)) {
                userOutstandingBalance.setOutStandingAmount(userOutstandingBalance.getOutStandingAmount().subtract(amountPerUser));
            }
        }

        groupExpenseTableService.updateGroupExpenseTable(userOutstandingBalances);
        System.out.println("Expense added to group expense table");

        return getSettlement(userOutstandingBalances);

    }


    public List<SettlementTransaction> getSettlement(List<UserOutstandingBalances> userOutstandingBalances) {
        Map<String, BigDecimal> transactions = new HashMap<>();
        for(UserOutstandingBalances userOutstandingBalance : userOutstandingBalances) {
            transactions.put(userOutstandingBalance.getUserId(), userOutstandingBalance.getOutStandingAmount());
        }
        return expenseBalancer.initializeQueue(transactions);
    }


    public List<UserOutstandingBalances> getGroupExpenseTableUserExpenseMap(String groupId) {
        Group group = groupService.getGroupById(groupId);
        return group.getUserOutstandingBalances();
    }
}
