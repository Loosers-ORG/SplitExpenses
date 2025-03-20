package com.loosers.org.splitExpenses.service;

import com.loosers.org.splitExpenses.model.Group;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GroupService {

    List<Group> groups;

    public void addGroup(Group group) {
        groups.add(group);
    }

    public List<Group> getGroups() {
        return groups;
    }
}
