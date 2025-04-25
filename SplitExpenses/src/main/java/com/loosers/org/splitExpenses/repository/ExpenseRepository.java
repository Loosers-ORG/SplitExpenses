package com.loosers.org.splitExpenses.repository;

import com.loosers.org.splitExpenses.model.Expense;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExpenseRepository extends JpaRepository<Expense, String> {
    List<Expense> findAllByGroup_GroupId(String groupId);

//    @Query("SELECT e FROM Expense e WHERE e.group.groupId = :groupId")
//    List<Expense> findByGroupId(@Param("groupId") String groupId);

}
