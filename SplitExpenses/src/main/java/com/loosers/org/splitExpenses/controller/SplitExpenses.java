package com.loosers.org.splitExpenses.controller;

import com.loosers.org.splitExpenses.dao.ExpenseDao;
import com.loosers.org.splitExpenses.dao.GroupDao;
import com.loosers.org.splitExpenses.dao.UserDao;
import com.loosers.org.splitExpenses.model.*;
import com.loosers.org.splitExpenses.service.*;
import com.loosers.org.splitExpenses.utils.IdGenerator;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@CrossOrigin(origins = {"https://splitexpenses-1.onrender.com/", "http://localhost:5173/"})
public class SplitExpenses {

    @Autowired
    UserService userService;

    @Autowired
    ExpenseService expenseService;

    @Autowired
    ModelMapper modelMapper;

    @Autowired
    SettlementTransactionService settlementTransactionService;;

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
    public ResponseEntity<List<UserDao>> getUsers() {
        List<User> users = userService.getUsers();
        List<UserDao> userDaos = users.stream().map(user -> modelMapper.map(user, UserDao.class)).toList();
        return new ResponseEntity<>(userDaos, HttpStatus.OK);
    }

    @GetMapping("/group/users")
    public ResponseEntity<List<UserDao>> getUsers(@RequestParam String groupId) {
        List<User> users =  groupService.getUsers(groupId);
        List<UserDao> userDaos = users.stream().map(user -> modelMapper.map(user, UserDao.class)).toList();
        return new ResponseEntity<>(userDaos, HttpStatus.OK);
    }


    @GetMapping("/expense")
    public ResponseEntity<List<ExpenseDao>> getExpenses(@RequestParam String groupId) {
        List<Expense> expenses = groupService.getExpensesInGroup(groupId);
        List<ExpenseDao> result = new ArrayList<>();
        for (Expense expense : expenses) {
            ExpenseDao expenseDao = modelMapper.map(expense, ExpenseDao.class);
            result.add(expenseDao);
        }
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @GetMapping("/group/expense")
    public ResponseEntity<List<Expense>> getGroupExpenses(@RequestParam String groupId) {
        List<Expense> groupExpenses = expenseService.getExpensesByGroup(groupId);
        return new ResponseEntity<>(groupExpenses, HttpStatus.OK);
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

    @GetMapping("/settlement")
    public ResponseEntity<List<SettlementTransaction>> getSettlement(@RequestParam String groupId) {
        List<SettlementTransaction> settlementTransactions = settlementTransactionService.getSettlementByGroup(groupId);
        return new ResponseEntity<>(settlementTransactions, HttpStatus.OK);
    }

    @PostMapping("/group")
    public ResponseEntity<?> addGroup(@RequestBody Group group) {
         groupService.addGroup(group);
         return new ResponseEntity<>("Group added successfully", HttpStatus.CREATED);
    }

    @GetMapping("/group")
    public ResponseEntity<List<GroupDao>> getGroups(@RequestParam String userId) {
        List<Group> groups = userService.getGroupsByUserId(userId);
        List<GroupDao> result = groups.stream()
                .map(group -> modelMapper.map(group, GroupDao.class))
                .toList();
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @GetMapping("/expenses")
    public ResponseEntity<?> getExpenseMap(@RequestParam String groupId) {
        return new ResponseEntity<>(groupExpenseService.getGroupExpenseTableUserExpenseMap(groupId), HttpStatus.OK);
    }

}