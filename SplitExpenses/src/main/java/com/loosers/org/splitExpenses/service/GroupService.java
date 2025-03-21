package com.loosers.org.splitExpenses.service;

import com.loosers.org.splitExpenses.model.Expense;
import com.loosers.org.splitExpenses.model.Group;
import com.loosers.org.splitExpenses.model.GroupExpenseTable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class GroupService {

    List<Group> groups;

    @Autowired
    GroupExpenseService groupExpenseService;

    GroupService() {
        groups = new ArrayList<>();
    }
    public void addGroup(Group group) {
        group.setExpenses(new ArrayList<>());
        groups.add(group);
        for(String userId: group.getUsers())
            groupExpenseService.addUser(userId);
    }

    public List<Group> getGroups(String userId) {
        return groups.stream().filter(g -> g.getUsers().contains(userId)).toList();
    }

    public void addExpenseToGroup(String expenseId, String groupId) {
        Group group = groups.stream().filter(g -> g.getGroupId().equals(groupId)).findFirst().orElse(null);
        if (group == null) {
            throw new RuntimeException("Group with ID " + groupId + " not found.");
        }
        group.getExpenses().add(expenseId);
    }

    public List<String> getExpensesInGroup(String groupId) {
        Group group = groups.stream().filter(g -> g.getGroupId().equals(groupId)).findFirst().orElse(null);
        if (group == null) {
            throw new RuntimeException("Group with ID " + groupId + " not found.");
        }
        return group.getExpenses();
    }

    public Group getGroupById(String groupId) {
        Group group = groups.stream().filter(g -> g.getGroupId().equals(groupId)).findFirst().orElse(null);
        if (group == null) {
            throw new RuntimeException("Group with ID " + groupId + " not found.");
        }
        return group;
    }
}
