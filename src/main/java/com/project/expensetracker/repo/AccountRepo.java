package com.project.expensetracker.repo;

import com.project.expensetracker.entity.Account;
import com.project.expensetracker.enums.AccountType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AccountRepo extends JpaRepository<Account,Long> {

    Optional<Account> findByIdAndIsActiveTrue(Long id);

    boolean existsByUser_EmailAndAccountTypeAndIsActiveTrue(String email, AccountType accountType);

    @Query("""
    select count(a) = :listCount
    from Account a
    where a.user.id = :userId
    and a.isActive = true
    and a.id in :accounts
""")
    boolean existsByUserIdAndAccount(String userId, List<Long> accounts, int listCount);

    @Query("select a from Account a where a.user.email = :userId and a.isActive = true")
    List<Account> findByUserIdAndActive(String userId);

    @Query("select a from Account a where a.user.id = :userId and a.isActive = true order by a.createdAt asc")
    List<Account> findAllByUserIdAndIsActiveTrueOrderByCreatedAtAsc(String userId);

    @Query("select a from Account a where a.id = :id and a.user.email = :userId and a.isActive = true")
    Account findByIdAndUserId(Long id, String userId);
}
