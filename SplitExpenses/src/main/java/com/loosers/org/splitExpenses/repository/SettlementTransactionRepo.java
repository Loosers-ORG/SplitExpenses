package com.loosers.org.splitExpenses.repository;

import com.loosers.org.splitExpenses.model.SettlementTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SettlementTransactionRepo extends JpaRepository<SettlementTransaction, String> {
    List<SettlementTransaction> findAllByGroupId(String groupId);
}
