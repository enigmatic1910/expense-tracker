package com.project.expensetracker.entity;

import com.project.expensetracker.enums.TransactionBehavior;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PaymentMode {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Enumerated(EnumType.STRING)
    TransactionBehavior type;

    @OneToMany(mappedBy = "paymentMode", fetch = FetchType.LAZY)
    private Set<UserConfig> userConfig;

    @OneToMany(mappedBy = "paymentMode", fetch = FetchType.LAZY)
    private Set<Transaction> transactions;
}
