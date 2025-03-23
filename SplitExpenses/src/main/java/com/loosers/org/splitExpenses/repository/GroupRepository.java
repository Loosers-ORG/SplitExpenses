package com.loosers.org.splitExpenses.repository;

import com.loosers.org.splitExpenses.model.Group;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GroupRepository extends JpaRepository<Group, String> {
}
