package com.project.expensetracker.repo;

import com.project.expensetracker.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AccountRepo extends JpaRepository<Account,Long> {

    @Query("""
    select count(a) = :listCount
    from Account a
    where a.user.id = :userId
    and a.id in :accounts
""")
    boolean existsByUserIdAndAccount(String userId, List<Long> accounts, int listCount);
}
