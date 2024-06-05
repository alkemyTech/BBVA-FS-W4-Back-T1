package com.magicdogs.alkywall.entities;

import com.magicdogs.alkywall.enums.TransactionConcept;
import com.magicdogs.alkywall.enums.TypeTransaction;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


import java.time.LocalDateTime;


@Data
@Entity
@Table(name="transactions")
@NoArgsConstructor
@AllArgsConstructor

public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column (name="idTransaction", nullable = false)
    private Long idTransaction;

    @Column(name="amount", nullable = false)
    private Double amount;

    @Column (name="type", nullable = false)
    @Enumerated(EnumType.STRING)
    private TypeTransaction type;

    @Column (name="concept", nullable = false)
    @Enumerated(EnumType.STRING)
    private TransactionConcept concept;

    @Column(name="description")
    private String description;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="idAccount", nullable = false)
    private Account account;

    @Column(name="transactionDate")
    private LocalDateTime transactionDate;

    public Transaction(Double amount, TypeTransaction typeTransaction, TransactionConcept concept, String description, Account account) {
        this.amount = amount;
        this.type = typeTransaction;
        this.concept = concept;
        this.description = description;
        this.account = account;
    }

    @PrePersist
    protected void onCreate() {
        transactionDate = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        transactionDate = LocalDateTime.now();
    }
}
