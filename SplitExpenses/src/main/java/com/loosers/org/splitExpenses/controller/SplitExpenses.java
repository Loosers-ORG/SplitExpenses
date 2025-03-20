package com.loosers.org.splitExpenses.controller;

    import com.loosers.org.splitExpenses.model.Expense;
    import com.loosers.org.splitExpenses.model.Group;
    import com.loosers.org.splitExpenses.model.User;
    import com.loosers.org.splitExpenses.service.ExpenseService;
    import com.loosers.org.splitExpenses.service.GroupService;
    import com.loosers.org.splitExpenses.service.UserService;
    import org.springframework.beans.factory.annotation.Autowired;
    import org.springframework.web.bind.annotation.*;

    @RestController
    public class SplitExpenses {

        @Autowired
        UserService userService;

        @Autowired
        ExpenseService expenseService;

        @Autowired
        GroupService groupService;

        @GetMapping("/hello")
        public String hello() {
            return "Hello World";
        }

        @PostMapping("/addUser")
        public String addUser(@RequestBody User user) {
            userService.addUser(user);
            return "User added successfully";
        }

        @PostMapping("/addExpense")
        public String addExpense(@RequestBody Expense expense) {
            expenseService.addExpense(expense);
            return "Expense added successfully";
        }

        @PostMapping("/addGroup")
        public String addGroup(@RequestBody Group group) {
            groupService.addGroup(group);
            return "Group added successfully";
        }
    }