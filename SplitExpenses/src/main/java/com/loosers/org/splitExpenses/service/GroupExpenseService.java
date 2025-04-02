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
import java.util.List;

@Service
public class GroupExpenseService {

    UserOutstandingBalances userOutstandingBalances;

    @Autowired
    GroupService groupService;

    @Autowired
    ExpenseBalancer expenseBalancer;

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
        return expenseBalancer.simplifyExpenses(group.getGroupId(),userOutstandingBalances);
    }


    public List<UserOutstandingBalances> getGroupExpenseTableUserExpenseMap(String groupId) {
        Group group = groupService.getGroupById(groupId);
        return group.getUserOutstandingBalances();
    }
}
