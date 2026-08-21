package com.project.expensetracker.repo;

import com.project.expensetracker.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountRepo extends JpaRepository<Account,Long> {

    @Query("""
    select count(a) > 0
    from Account a
    join a.user u
    where u.id = :userId
    and a.id = :accountId
""")
    boolean existsByUserIdAndAccount(String userId, Long accountId);
}
