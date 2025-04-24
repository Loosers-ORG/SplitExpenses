package com.loosers.org.splitExpenses.dao;

import com.loosers.org.splitExpenses.model.User;
import jakarta.persistence.Column;
import jakarta.persistence.Id;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class ExpenseDao {
    private String expenseId;
    private String name;
    private String paidBy;
    private BigDecimal amount;
    List<UserDao> users;
}
