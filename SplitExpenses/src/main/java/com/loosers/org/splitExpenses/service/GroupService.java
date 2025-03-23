package com.loosers.org.splitExpenses.service;

import com.loosers.org.splitExpenses.model.Expense;
import com.loosers.org.splitExpenses.model.Group;
import com.loosers.org.splitExpenses.model.GroupExpenseTable;
import com.loosers.org.splitExpenses.model.User;
import com.loosers.org.splitExpenses.repository.GroupRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class GroupService {

    @Autowired
    GroupExpenseService groupExpenseService;

    @Autowired
    UserService userService;

    @Autowired
    GroupRepository groupRepository;

    public void addGroup(Group group) {
        groupRepository.save(group);
        for(User user: group.getUsers())
            groupExpenseService.addUser(user.getEmail());
    }

//    public List<Group> getGroups(String userId) {
//        return groups.stream().filter(g -> g.getUsers().contains(userId)).toList();
//    }

    public void addExpenseToGroup(Expense expense, String groupId) {
        Optional<Group> foundGroup = groupRepository.findById(groupId);
        if (foundGroup.isEmpty()) {
            throw new RuntimeException("Group with ID " + groupId + " not found.");
        }
        Group group = foundGroup.get();
        group.getExpenses().add(expense);
    }

    public List<Expense> getExpensesInGroup(String groupId) {
        Optional<Group> foundGroup = groupRepository.findById(groupId);
        if (foundGroup.isEmpty()) {
            throw new RuntimeException("Group with ID " + groupId + " not found.");
        }
        Group group = foundGroup.get();
        return group.getExpenses();
    }

    public Group getGroupById(String groupId) {
        Optional<Group> foundGroup = groupRepository.findById(groupId);
        if (foundGroup.isEmpty()) {
            throw new RuntimeException("Group with ID " + groupId + " not found.");
        }
        return foundGroup.get();
    }
}
