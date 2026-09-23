package com.project.expensetracker.repo;

import com.project.expensetracker.entity.AiParsingTask;
import com.project.expensetracker.entity.User;
import com.project.expensetracker.enums.Status;
import org.hibernate.query.spi.Limit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AiParsingRepo extends JpaRepository<AiParsingTask, Long> {

    List<AiParsingTask> findAllByStatusOrderByCreatedAtAsc(Status status, Limit limit);

    @Query("SELECT apt " +
            "FROM AiParsingTask apt " +
            "JOIN FETCH apt.user u " +
            "WHERE apt.status = :status " +
            "ORDER BY apt.createdAt ASC")
    List<AiParsingTask> findAllByStatusWithUserOrderByCreatedAtAsc(Status status, Limit limit);
}
