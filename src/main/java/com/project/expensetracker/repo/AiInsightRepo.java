package com.project.expensetracker.repo;

import com.project.expensetracker.entity.AiInsightTask;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.time.LocalDateTime;

@Repository
public interface AiInsightRepo extends JpaRepository<AiInsightTask, Long> {
    long countByUserEmailAndCreatedAtAfter(String email, LocalDateTime after);

    @Query("select a from AiInsightTask a where a.user.email = :userId order by a.createdAt desc")
    Optional<AiInsightTask> findFirstByAppUserIdOrderByCreatedAtDesc(String userId);
}
