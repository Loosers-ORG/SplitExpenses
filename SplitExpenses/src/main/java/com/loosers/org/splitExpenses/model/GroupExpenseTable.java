package com.loosers.org.splitExpenses.model;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Map;

@Data
public class GroupExpenseTable {
    private String groupId;
    private Map<String, BigDecimal> userExpenseMap;
}
