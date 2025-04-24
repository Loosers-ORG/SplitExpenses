package com.loosers.org.splitExpenses.service;

import com.loosers.org.splitExpenses.model.Expense;
import com.loosers.org.splitExpenses.model.Group;
import com.loosers.org.splitExpenses.model.User;
import com.loosers.org.splitExpenses.repository.GroupRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class GroupService {

    @Autowired
    UserService userService;

    @Autowired
    GroupExpenseTableService groupExpenseTableService;

    @Autowired
    GroupRepository groupRepository;

    public void addGroup(Group group) {
        List<User> usersInGroup = userService.getUsersById(group.getUserIds());
        group.setUsers(usersInGroup);
        groupRepository.save(group);
        groupExpenseTableService.createGroupExpenseTable(group);
    }

    public void addExpenseToGroup(Expense expense, String groupId) {
        Group group = getGroupById(groupId);
        expense.setGroup(group);
        group.getExpenses().add(expense);
        groupRepository.save(group);
    }

    public List<Expense> getExpensesInGroup(String groupId) {
        Group group = getGroupById(groupId);
        return group.getExpenses();
    }

    public Group getGroupById(String groupId) {
        Optional<Group> foundGroup = groupRepository.findById(groupId);
        if (foundGroup.isEmpty()) {
            throw new RuntimeException("Group with ID " + groupId + " not found.");
        }
        return foundGroup.get();
    }

    public List<User> getUsers(String groupId) {
        Group group = getGroupById(groupId);
        return group.getUsers();
    }
}
