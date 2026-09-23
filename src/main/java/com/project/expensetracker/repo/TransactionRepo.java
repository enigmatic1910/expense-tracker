package com.project.expensetracker.repo;

import com.project.expensetracker.entity.Transaction;
import com.project.expensetracker.dto.CategorySpendDto;
import com.project.expensetracker.enums.TransactionType;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.time.LocalDate;
import com.project.expensetracker.dto.TransactionDailySpendDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Repository
public interface TransactionRepo extends JpaRepository<Transaction, Long> {

    @Query("select t from Transaction t where t.user.id = :userId")
    List<Transaction> findAllByUserId(String userId);

    Page<Transaction> findAllByUserId(String userId, Pageable pageable);

    @Query("select t from Transaction t where t.user.id = :userId and t.transactionType = :transactionType")
    List<Transaction> findAllByUserIdAndTransactionType(String userId, TransactionType transactionType);

    Page<Transaction> findAllByUserIdAndTransactionType(
            String userId,
            TransactionType transactionType,
            Pageable pageable
    );

    @Query("""
            select new com.project.expensetracker.dto.CategorySpendDto(
                c.name,
                sum(abs(t.amount))
            )
            from Transaction t
            join t.category c
            where t.user.id = :userId
              and t.transactionType = com.project.expensetracker.enums.TransactionType.EXPENSE
            group by c.name
            order by sum(abs(t.amount)) desc
            """)
    List<CategorySpendDto> findCategorySpendByUserId(String userId);

    @Query("select coalesce(sum(abs(t.amount)), 0.0) from Transaction t "
            + "where t.user.id = :userId and t.transactionType = :transactionType")
    Double sumAmountByUserIdAndTransactionType(String userId, TransactionType transactionType);

    @Query("select coalesce(sum(abs(t.amount)), 0.0) from Transaction t "
            + "where t.card.id = :cardId and t.user.id = :userId "
            + "and t.transactionType = com.project.expensetracker.enums.TransactionType.EXPENSE")
    Double sumExpenseByCardId(String userId, String cardId);

    @Query("""
            select new com.project.expensetracker.dto.TransactionDailySpendDto(
                t.transactionDate,
                sum(abs(t.amount))
            )
            from Transaction t
            where t.user.id = :userId
              and t.transactionType = com.project.expensetracker.enums.TransactionType.EXPENSE
              and t.transactionDate between :from and :to
            group by t.transactionDate
            order by t.transactionDate
            """)
    List<TransactionDailySpendDto> findDailyExpenseSpend(
            String userId,
            LocalDate from,
            LocalDate to
    );

    @Modifying
    @Transactional
    @Query("delete from Transaction t where t.id = :transactionId and t.user.id = :userId")
    void deleteByIdAndUserId(Long transactionId, String userId);

    List<Transaction> findAllByTransferId(String transferId);

    List<Transaction> findAllByUserIdAndTransactionDateBetween(
            String userId,
            LocalDate from,
            LocalDate to
    );
}
