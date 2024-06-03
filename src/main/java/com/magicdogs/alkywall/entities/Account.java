package com.magicdogs.alkywall.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "accounts")
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idAccount")
    private Long idAccount;

    @Enumerated(EnumType.STRING)
    @Column(name = "currency", nullable = false)
    private CurrencyType currency;

    @Column(name = "transactionLimit", nullable = false)
    private Double transactionLimit;

    @Column(name = "balance", nullable = false)
    private Double balance;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idUser")
    private User user;

    @Column(name = "creationDate")
    private LocalDateTime creationDate;

    @Column(name = "updateDate")
    private LocalDateTime updateDate;

    @Column(name = "softDelete", nullable = false)
    private Integer softDelete;

    @Column(name="cbu", nullable=false)
    private String cbu;

    @OneToMany(mappedBy = "account", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Transaction> transactions;

    @OneToMany(mappedBy = "account", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<FixedTermDeposit> fixedTermDeposits;

    public Account(CurrencyType currencyType, double transactionLimit, double balance, User user, int softDelete, String cbu) {
        this.currency = currencyType;
        this.transactionLimit = transactionLimit;
        this.balance = balance;
        this.user = user;
        this.softDelete = softDelete;
        this.cbu = cbu;
    }

    @PrePersist
    protected void onCreate() {
        creationDate = LocalDateTime.now();
        updateDate = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updateDate = LocalDateTime.now();
    }

    public Transaction searchTransactionById(Long id){
        return transactions.stream().filter(t -> t.getIdTransaction().equals(id)).findFirst().orElse(null);

    }
}
