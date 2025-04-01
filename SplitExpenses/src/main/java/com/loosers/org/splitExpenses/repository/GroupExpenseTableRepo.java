package com.loosers.org.splitExpenses.repository;


import com.loosers.org.splitExpenses.model.UserOutstandingBalances;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GroupExpenseTableRepo extends JpaRepository<UserOutstandingBalances, String> {
}
