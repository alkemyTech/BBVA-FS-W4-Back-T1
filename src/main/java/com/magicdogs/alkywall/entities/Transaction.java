package com.magicdogs.alkywall.entities;

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

    @Column(name="description")
    private String description;

    //@Column (name="accountId", nullable = false)
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="idAccount", nullable = false)
    private Account account;

    @Column(name="transactionDate")
    private LocalDateTime transactionDate;

    public Transaction(double amount, TypeTransaction typeTransaction, String description, Account account) {
        this.amount = amount;
        this.type = typeTransaction;
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
