package com.loosers.org.splitExpenses.repository;

import com.loosers.org.splitExpenses.model.SettlementTransaction;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SettlementTransactionRepo extends JpaRepository<SettlementTransaction, String> {
    List<SettlementTransaction> findAllByGroupId(String groupId);
//    void deleteAllByGroupId(String groupId);

    @Modifying
    @Transactional
    @Query("DELETE FROM SettlementTransaction s WHERE s.groupId = :groupId")
    void deleteByGroupId(@Param("groupId") String groupId);
}
