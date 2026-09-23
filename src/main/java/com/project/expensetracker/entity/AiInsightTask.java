package com.project.expensetracker.entity;

import com.project.expensetracker.enums.InsightType;
import com.project.expensetracker.enums.Status;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class AiInsightTask {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String period;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private Status status = Status.PENDING;

    @CreationTimestamp
    private LocalDateTime createdAt;

    private LocalDateTime completedAt;

    @Column(columnDefinition = "text")
    private String errorMessage;

    @Column(columnDefinition = "text")
    private String summary;

    private String topSpendingCategory;

    private Float topSpendingPercentage;

    @Column(columnDefinition = "text")
    private String topSpendingInsight;

    @Column(columnDefinition = "text")
    private String anomalies;

    @Column(columnDefinition = "text")
    private String actionableTips;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="user_id", nullable = false)
    private User user;
}
