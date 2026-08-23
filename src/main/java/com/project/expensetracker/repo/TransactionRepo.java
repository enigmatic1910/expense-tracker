package com.project.expensetracker.repo;

import com.project.expensetracker.entity.Transaction;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransactionRepo extends JpaRepository<Transaction, Long> {

    @Query("select t from Transaction t where t.user.id = :userId")
    List<Transaction> findAllByUserId(String userId);

    @Modifying
    @Transactional
    @Query("delete from Transaction t where t.id = :transactionId and t.user.id = :userId")
    void deleteByIdAndUserId(Long transactionId, String userId);

    List<Transaction> findAllByTransferId(String transferId);
}
