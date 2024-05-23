package com.magicdogs.alkywall.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "accounts")
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idAccount")
    private Long id;
    @Enumerated(EnumType.STRING)
    @NotNull
    @Column(name = "currency")
    private CurrencyType currency;
    @NotNull
    @Column(name = "transactionLimit")
    private Double transactionLimit;
    @NotNull
    @Column(name = "balance")
    private Double balance;
    @NotNull
    @Column(name = "createDate")
    private LocalDateTime creationDate;
    @NotNull
    @Column(name = "updateDate")
    private LocalDateTime updateDate;
    @NotNull
    @Column(name = "softDelete")
    private Boolean softDelete;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_user")
    private User user;

    @PrePersist
    protected void onCreate() {
        creationDate = LocalDateTime.now();
        updateDate = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updateDate = LocalDateTime.now();
    }
}
