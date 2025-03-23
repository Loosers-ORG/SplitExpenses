package com.loosers.org.splitExpenses.controller;

import com.loosers.org.splitExpenses.model.*;
import com.loosers.org.splitExpenses.service.ExpenseService;
import com.loosers.org.splitExpenses.service.GroupExpenseService;
import com.loosers.org.splitExpenses.service.GroupService;
import com.loosers.org.splitExpenses.service.UserService;
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

    @GetMapping("/hello")
    public String hello() {
        return "Hello World";
    }

    @PostMapping("/addUser")
    public String addUser(@RequestBody User user) {
        userService.addUser(user);

        return "User added successfully";
    }

    @GetMapping("/getUsers")
    public ResponseEntity<List<User>> getUsers() {
        List<User> users =  userService.getUsers();
        return new ResponseEntity<>(users, HttpStatus.OK);
    }

    @PostMapping("/addExpense")
    public String addExpense(@RequestBody Expense expense, @RequestParam String groupId) {
        return expenseService.addExpenseToGroup(expense, groupId);
    }

    @PostMapping("/addGroup")
    public String addGroup(@RequestBody Group group) {
        groupService.addGroup(group);
        return "Group added successfully";
    }

    @GetMapping("/getExpenseMap")
    public String getExpenseMap() {
        return groupExpenseService.getGroupExpenseTableUserExpenseMap().toString();
    }

}