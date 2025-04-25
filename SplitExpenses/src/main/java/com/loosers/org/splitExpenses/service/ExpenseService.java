package com.loosers.org.splitExpenses.service;

import com.loosers.org.splitExpenses.model.Expense;
import com.loosers.org.splitExpenses.model.Group;
import com.loosers.org.splitExpenses.model.SettlementTransaction;
import com.loosers.org.splitExpenses.model.User;
import com.loosers.org.splitExpenses.repository.ExpenseRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

    public void updateExpense(Expense expense) {
        expenseRepository.save(expense);
    }

    public List<SettlementTransaction> addExpenseToGroup(Expense expense, String groupId) {
        List<User> usersInExpense = userService.getUsersById(expense.getUsersIncludedInExpense());
        expense.setUsers(usersInExpense);
//        groupService.addExpenseToGroup(expense, groupId);
        updateExpenseToGroup(expense, groupId);
        return groupExpenseService.addExpense(expense);
    }

    @Transactional
    private void updateExpenseToGroup(Expense expense, String groupId){
        long start = System.currentTimeMillis();
        Group group = groupService.getGroupById(groupId);
        expense.setGroup(group);
        expenseRepository.save(expense);
        long end = System.currentTimeMillis();
        System.out.println("addExpenseToGroup() took: " + (end - start) + " ms");
    }

    public Expense getExpenseById(String expenseId) {
        Optional<Expense> foundExpense = expenseRepository.findById(expenseId);
        if (foundExpense.isEmpty()) {
            throw new RuntimeException("Expense with ID " + expenseId + " not found.");
        }
        return foundExpense.get();
    }

    public List<Expense> getExpensesByGroup(String groupId) {
        return expenseRepository.findAllByGroup_GroupId(groupId);
    }

    public List<SettlementTransaction> modifyExpense(Expense expense){
        Expense expenseInDb = getExpenseById(expense.getExpenseId());
        groupExpenseService.revertExpense(expenseInDb, true);
        return addExpenseToGroup(expense, expenseInDb.getGroup().getGroupId());
    }

    public List<SettlementTransaction> deleteExpense(String expenseId) {
        Expense expenseInDb = getExpenseById(expenseId);

        List<SettlementTransaction> settlementTransactionList = groupExpenseService.revertExpense(expenseInDb, false);
        deleteExpenseFromDb(expenseId);
        return settlementTransactionList;
    }

    private void deleteExpenseFromDb(String expenseId) {
        expenseRepository.deleteById(expenseId);
    }

}