package com.loosers.org.splitExpenses.service;

    import com.loosers.org.splitExpenses.ExpenseCalculator.ExpenseBalancer;
    import com.loosers.org.splitExpenses.model.*;
    import org.springframework.beans.factory.annotation.Autowired;
    import org.springframework.stereotype.Service;

    import java.math.BigDecimal;
    import java.math.RoundingMode;
    import java.util.ArrayList;
    import java.util.List;

    @Service
    public class GroupExpenseService {

        @Autowired
        GroupService groupService;

        @Autowired
        ExpenseBalancer expenseBalancer;

        @Autowired
        GroupExpenseTableService groupExpenseTableService;

        @Autowired
        SettlementTransactionService settlementTransactionService;

        private BigDecimal calculateAmountPerUser(BigDecimal amount, int numberOfUsers) {
            return amount.divide(BigDecimal.valueOf(numberOfUsers), 2, RoundingMode.HALF_UP);
        }

        private void updateUserOutstandingBalances(List<UserOutstandingBalances> userOutstandingBalances, String paidBy, List<String> usersIncludedInExpense, BigDecimal amount, BigDecimal amountPerUser, boolean isRevert) {
            for (UserOutstandingBalances userOutstandingBalance : userOutstandingBalances) {
                String userId = userOutstandingBalance.getUserId();
                if (userId.equals(paidBy)) {
                    userOutstandingBalance.setOutStandingAmount(userOutstandingBalance.getOutStandingAmount().add(isRevert ? amount.subtract(amountPerUser).negate() : amount.subtract(amountPerUser)));
                } else if (usersIncludedInExpense.contains(userId)) {
                    userOutstandingBalance.setOutStandingAmount(userOutstandingBalance.getOutStandingAmount().add(isRevert ? amountPerUser : amountPerUser.negate()));
                }
            }
        }

        public List<SettlementTransaction> addExpense(Expense expense) {
            BigDecimal amount = expense.getAmount();
            String paidBy = expense.getPaidBy();
            List<String> usersIncludedInExpense = expense.getUsersIncludedInExpense();
            Group group = expense.getGroup();
            List<UserOutstandingBalances> userOutstandingBalances = group.getUserOutstandingBalances();

            BigDecimal amountPerUser = calculateAmountPerUser(amount, usersIncludedInExpense.size());
            updateUserOutstandingBalances(userOutstandingBalances, paidBy, usersIncludedInExpense, amount, amountPerUser, false);

            groupExpenseTableService.updateGroupExpenseTable(userOutstandingBalances);
            System.out.println("Expense added to group expense table");
            return expenseBalancer.simplifyExpenses(group.getGroupId(), userOutstandingBalances);
        }

        public List<SettlementTransaction> revertExpense(Expense expense, boolean isModify) {
            BigDecimal amount = expense.getAmount();
            String paidBy = expense.getPaidBy();
            List<User> usersInExpense = expense.getUsers();
            List<String> usersIncludedInExpense = new ArrayList<>();
            for (User user : usersInExpense) {
                usersIncludedInExpense.add(user.getEmail());
            }

            Group group = expense.getGroup();
            List<UserOutstandingBalances> userOutstandingBalances = group.getUserOutstandingBalances();

            BigDecimal amountPerUser = calculateAmountPerUser(amount, usersIncludedInExpense.size());
            updateUserOutstandingBalances(userOutstandingBalances, paidBy, usersIncludedInExpense, amount, amountPerUser, true);

            System.out.println("Expense reverted in group expense table" + isModify);
            groupExpenseTableService.updateGroupExpenseTable(userOutstandingBalances);

            System.out.println("Settlement transactions deleted");
            if (!isModify) return expenseBalancer.simplifyExpenses(group.getGroupId(), userOutstandingBalances);

            return null;
        }

        public List<UserOutstandingBalances> getGroupExpenseTableUserExpenseMap(String groupId) {
            Group group = groupService.getGroupById(groupId);
            return group.getUserOutstandingBalances();
        }
    }