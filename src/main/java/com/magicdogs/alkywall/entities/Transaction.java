package com.magicdogs.alkywall.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.antlr.v4.runtime.misc.NotNull;

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
    @NotNull
    private Double amount;

    @Column (name="type", nullable = false)
    @Enumerated(EnumType.STRING)
    private TypeTransaction type;

    @Column(name="description")
    private String description;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="accountId", nullable = false)
    private Account account;

    @Column(name="transactionDate")
    private LocalDateTime transactionDate;

    @PrePersist
    protected void onCreate() {
        transactionDate = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        transactionDate = LocalDateTime.now();
    }



}
