package com.project.expensetracker.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;


import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name="app_user")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    private String name;
    private String email;
    private String password;

    @CreationTimestamp
    private LocalDateTime createdAt;
    @UpdateTimestamp
    private LocalDateTime updatedAt;
    @UpdateTimestamp
    private LocalDateTime lastLoginAt;
    @UpdateTimestamp
    private LocalDateTime lastInsightAt;

    @OneToOne(mappedBy = "user")
    private UserConfig userConfig;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private List<Account> accounts;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private Set<Transaction> transactions;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private Set<AiParsingTask> aiParsingTasks;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private Set<AiInsightTask> aiInsightTasks;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private List<Category> categories;
}
