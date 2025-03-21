package com.loosers.org.splitExpenses.model;

import lombok.Data;

import java.util.Map;

@Data
public class GroupExpenseTable {
    private String groupId;
    private Map<String, Double> userExpenseMap;
}
