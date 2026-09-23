package com.project.expensetracker.repo;

import com.project.expensetracker.entity.Card;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CardRepo extends JpaRepository<Card, String> {

    @Query("select c from Card c where c.user.email = :userId and c.isActive = true")
    List<Card> findByUser(String userId);

    @Query("select c from Card c where c.id = :cardId and c.user.email = :userId")
    Card findByIdAndUserEmail(String cardId, String userId);

    @Query("select c from Card c where c.id = :cardId and c.user.email = :userId and c.isActive = true")
    Card findActiveByIdAndUserEmail(String cardId, String userId);
}
