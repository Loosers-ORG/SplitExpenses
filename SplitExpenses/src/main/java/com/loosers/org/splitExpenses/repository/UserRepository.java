package com.loosers.org.splitExpenses.repository;

import com.loosers.org.splitExpenses.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, String> {

}
