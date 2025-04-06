package com.loosers.org.splitExpenses.service;

import com.loosers.org.splitExpenses.model.Group;
import com.loosers.org.splitExpenses.model.UserOutstandingBalances;
import com.loosers.org.splitExpenses.model.User;
import com.loosers.org.splitExpenses.repository.GroupExpenseTableRepo;
import com.loosers.org.splitExpenses.utils.IdGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class GroupExpenseTableService {

    @Autowired
    GroupExpenseTableRepo groupExpenseTableRepo;

    @Autowired
    IdGenerator idGenerator;

    public void saveGroupExpenseTable(UserOutstandingBalances userOutstandingBalances) {
        groupExpenseTableRepo.save(userOutstandingBalances);
    }

    public void updateGroupExpenseTable(List<UserOutstandingBalances> userOutstandingBalancesList) {
        groupExpenseTableRepo.saveAll(userOutstandingBalancesList);
    }

    public UserOutstandingBalances getGroupExpenseTableById(String groupId, String userId) {
        String id = idGenerator.generteId(groupId, userId);
        return groupExpenseTableRepo.findById(id).orElse(null);
    }

    public void createGroupExpenseTable(Group group) {
        for(User user: group.getUsers()) {
            String id = idGenerator.generteId(group.getGroupId(), user.getEmail());
            saveGroupExpenseTable(new UserOutstandingBalances(id, user.getEmail(), BigDecimal.ZERO, group));
        }
    }

}
