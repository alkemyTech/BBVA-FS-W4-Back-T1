package com.magicdogs.alkywall.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "fixed_term_deposits")
public class FixedTermDeposit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idDeposit")
    private Long idDeposit;

    @Column(name = "amount", nullable = false)
    private Double amount;

    //@Column (name="accountId", nullable = false)
    @ManyToOne
    @JoinColumn(name = "idAccount", nullable = false)
    private Account account;

    @Column(name = "interest", nullable = false)
    private Double interest;

    @Column(name = "creationDate")
    private LocalDateTime creationDate;

    @Column(name = "closingDate")
    private LocalDateTime closingDate;

    @PrePersist
    protected void onCreate() {
        creationDate = LocalDateTime.now();
    }
}
