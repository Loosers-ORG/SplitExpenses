package com.loosers.org.splitExpenses.controller;

import com.loosers.org.splitExpenses.model.*;
import com.loosers.org.splitExpenses.service.ExpenseService;
import com.loosers.org.splitExpenses.service.GroupExpenseService;
import com.loosers.org.splitExpenses.service.GroupService;
import com.loosers.org.splitExpenses.service.UserService;
import com.loosers.org.splitExpenses.utils.IdGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class SplitExpenses {

    @Autowired
    UserService userService;

    @Autowired
    ExpenseService expenseService;

    @Autowired
    GroupService groupService;

    @Autowired
    GroupExpenseService groupExpenseService;

    @Autowired
    IdGenerator idGenerator;

    @GetMapping("/hello")
    public String hello() {
        return "Hello World";
    }

    @PostMapping("/user")
    public String addUser(@RequestBody User user) {
        userService.addUser(user);

        return "User added successfully";
    }

    @GetMapping("/users")
    public ResponseEntity<List<User>> getUsers() {
        List<User> users =  userService.getUsers();
        return new ResponseEntity<>(users, HttpStatus.OK);
    }

    @PostMapping("/expense")
    public ResponseEntity<List<SettlementTransaction>> addExpense(@RequestBody Expense expense, @RequestParam String groupId) {
        expense.setExpenseId(idGenerator.generteId(groupId, expense.getExpenseId()));
        List<SettlementTransaction> settlementTransactions = expenseService.addExpenseToGroup(expense,groupId);
        return new ResponseEntity<>(settlementTransactions, HttpStatus.CREATED);
    }

    @PutMapping("/expense")
    public ResponseEntity<?> updateExpense(@RequestBody Expense expense) {
        try {
            List<SettlementTransaction> settlementTransactions = expenseService.modifyExpense(expense);
            return new ResponseEntity<>(settlementTransactions, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Error updating expense: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/expense")
    public ResponseEntity<?> DeleteExpense(@RequestParam String expenseId) {
        try {
            List<SettlementTransaction> settlementTransactions = expenseService.deleteExpense(expenseId);
            return new ResponseEntity<>(settlementTransactions, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Error deleting expense: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/group")
    public ResponseEntity<?> addGroup(@RequestBody Group group) {
         groupService.addGroup(group);
         return new ResponseEntity<>("Group added successfully", HttpStatus.CREATED);
    }

    @GetMapping("/expenses")
    public ResponseEntity<?> getExpenseMap(@RequestParam String groupId) {
        return new ResponseEntity<>(groupExpenseService.getGroupExpenseTableUserExpenseMap(groupId), HttpStatus.OK);
    }

}