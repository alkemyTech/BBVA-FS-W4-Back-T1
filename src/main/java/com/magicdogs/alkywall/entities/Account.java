package com.magicdogs.alkywall.entities;

import com.magicdogs.alkywall.enums.AccountType;
import com.magicdogs.alkywall.enums.CurrencyType;
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
    @Column(name = "accountType", nullable = false)
    private AccountType accountType;

    @Enumerated(EnumType.STRING)
    @Column(name = "currency", nullable = false)
    private CurrencyType currency;

    @Column(name="cbu", nullable=false)
    private String cbu;

    @Column(name="alias", nullable=false)
    private String alias;

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

    @OneToMany(mappedBy = "account", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Transaction> transactions;

    @OneToMany(mappedBy = "account", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<FixedTermDeposit> fixedTermDeposits;


    public Account(AccountType accountType, CurrencyType currencyType, String cbu, String alias, Double transactionLimit, Double balance, User user, Integer softDelete) {
        this.accountType = accountType;
        this.currency = currencyType;
        this.cbu = cbu;
        this.alias = alias;
        this.transactionLimit = transactionLimit;
        this.balance = balance;
        this.user = user;
        this.softDelete = softDelete;
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